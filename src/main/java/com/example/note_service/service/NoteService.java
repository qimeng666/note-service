package com.example.note_service.service;

import com.example.note_service.entity.Note;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Service
public interface NoteService {
    Note create(Note note, Set<String> tagNames);
    List<Note> listByUser(Long userId);
    Note getById(Long id);
    Note update(Long id, Note note, Set<String> tagNames);
    void delete(Long id);
    List<Note> listByTag(String tagName);
}
