package com.example.note_service.controller;

import com.example.note_service.entity.Note;
import com.example.note_service.repository.NoteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/notes")
public class InternalNoteController {
    final private NoteRepository noteRepository;
    public InternalNoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }
    @GetMapping("/batch")
    public List<Note> getBatch(@RequestParam List<Long> ids) {
        return noteRepository.findAllById(ids);
    }

}
