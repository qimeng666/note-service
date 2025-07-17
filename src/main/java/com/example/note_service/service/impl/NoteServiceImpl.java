package com.example.note_service.service.impl;

import com.example.note_service.Dto.NoteEvent;
import com.example.note_service.client.UserClient;
import com.example.note_service.entity.Note;
import com.example.note_service.entity.Tag;
import com.example.note_service.repository.NoteRepository;
import com.example.note_service.repository.TagRepository;
import com.example.note_service.service.NoteService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final UserClient userClient;
    private final KafkaTemplate<String, NoteEvent> kafka;

    public NoteServiceImpl(NoteRepository noteRepository, TagRepository tagRepository, UserClient userClient, KafkaTemplate<String, NoteEvent> kafka) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
        this.userClient = userClient;
        this.kafka = kafka;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "userValidationFallback")
    @Retry(name = "userService", fallbackMethod = "createFallback")
    public Note create(Note note, Set<String> tagNames) {
        // 调用用户微服务来进行验证用户是否存在
        userClient.getById(note.getUserId());
        Set<Tag> tags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name)))).collect(Collectors.toSet());
        note.setTags(tags);
        Note savedNote = noteRepository.save(note);
        NoteEvent event = new NoteEvent(
                savedNote.getId(),
                savedNote.getUserId(),
                savedNote.getTitle(),
                Instant.now()
        );
        kafka.send("note-creation-event", savedNote.getId().toString(), event);
        log.info("Published NoteEvent for create: {}", event);
        return savedNote;
    }
    private Note userValidationFallback(Note note, Set<String> tagNames, Throwable t) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "无法创建笔记：用户服务故障",
                t
        );
    }

    private Note createFallback(Note note, Set<String> tagNames, Throwable t) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "无法创建笔记：服务暂时不可用",
                t
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> listByUser(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Note getById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found: " + id));
    }

    @Override
    @Transactional
    public Note update(Long id, Long userId, Note note, Set<String> tagNames) {
        Note existingNote = getById(id);
        if (!existingNote.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不能修改非自己的笔记");
        }
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        if (note.getUserId() != null) {
            existingNote.setUserId(note.getUserId());
        }
        Set<Tag> tags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name))))
                .collect(Collectors.toSet());
        existingNote.setTags(tags);
        Note updated = noteRepository.save(existingNote);

        // 发布事件
        NoteEvent event = new NoteEvent(
                updated.getId(),
                updated.getUserId(),
                updated.getTitle(),
                Instant.now()
        );
        kafka.send("note.events", updated.getId().toString(), event);
        log.info("Published NoteEvent for update: {}", event);
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Note note = getById(id);
        noteRepository.delete(note);
    }

    @Override
    @Transactional
    public List<Note> listByTag(String tagName) {
        return noteRepository.findByTagsName(tagName);
    }

    @Override
    public void deleteForUser(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found: " + noteId));
        if(!note.getUserId().equals(userId)){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "这不是你的笔记");
        }
        note.getTags().forEach(tag -> tag.getNotes().remove(note));
        noteRepository.delete(note);
    }

}
