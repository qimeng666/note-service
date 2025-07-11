package com.example.note_service.service.impl;

import com.example.note_service.entity.Note;
import com.example.note_service.entity.Tag;
import com.example.note_service.repository.NoteRepository;
import com.example.note_service.repository.TagRepository;
import com.example.note_service.service.NoteService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    public NoteServiceImpl(NoteRepository noteRepository, TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Note create(Note note, Set<String> tagNames) {
        Set<Tag> tags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name)))).collect(Collectors.toSet());
        note.setTags(tags);
        return noteRepository.save(note);
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
    public Note update(Long id, Note note, Set<String> tagNames) {
        Note existingNote = getById(id);
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
        return noteRepository.save(existingNote);
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
}
