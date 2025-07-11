package com.example.note_service.controller;

import com.example.note_service.Dto.NoteRequest;
import com.example.note_service.entity.Note;
import com.example.note_service.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/notes")
    public ResponseEntity<Note> create(@RequestBody NoteRequest req) {
        // Logic to create a note
        Note created = noteService.create(mapToEntity(req, req.getUserId()), req.getTags());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/users/{userId}/notes")
    public List<Note> listByUser(@PathVariable Long userId) {
        return noteService.listByUser(userId);
    }
    @GetMapping("/notes/{noteId}")
    public Note get(@PathVariable Long noteId) {
        return noteService.getById(noteId);
    }
    @PutMapping("/notes/{noteId}")
    public Note update(@PathVariable Long noteId, @RequestBody NoteRequest req) {
        return noteService.update(noteId, mapToEntity(req, req.getUserId()), req.getTags());
    }

    @DeleteMapping("/notes/{noteId}")
    public void delete(@PathVariable Long noteId) {
        noteService.delete(noteId);
    }

    @GetMapping("/notes")
    public List<Note> byTag(@RequestParam(required = false) String tag) {
      if(tag != null){
          return noteService.listByTag(tag);
      }
      else{
          return Collections.emptyList();
      }
    }
    private Note mapToEntity(NoteRequest req, Long userId) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());
        return note;
    }
}
