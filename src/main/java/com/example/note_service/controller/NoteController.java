package com.example.note_service.controller;

import com.example.note_service.Dto.NoteRequest;
import com.example.note_service.entity.Note;
import com.example.note_service.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import com.example.note_service.Dto.UserDto;
import com.example.note_service.client.UserClient;

@RestController
public class NoteController {

    private final NoteService noteService;
    private final UserClient userClient;

    public NoteController(NoteService noteService, UserClient userClient) {
        this.noteService = noteService;
        this.userClient = userClient;
    }

    @PostMapping("/notes")
    public ResponseEntity<Note> create(@AuthenticationPrincipal Jwt jwt, @RequestBody NoteRequest req) {
        Long userId = jwt.getClaim("userId");

        Note created = noteService.create(mapToEntity(req, userId), req.getTags());

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
    public Note update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long noteId, @RequestBody NoteRequest req) {
        Long userId = jwt.getClaim("userId");
        return noteService.update(noteId, userId, mapToEntity(req, userId), req.getTags());
    }

    @DeleteMapping("/notes/{noteId}")
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long noteId) {
        Long userId = jwt.getClaim("userId");
        noteService.deleteForUser(noteId, userId);
    }

    @GetMapping("/notes")
    public List<Note> byTag(@RequestParam(required = false) String tag) {
      if(tag != null && !tag.trim().isEmpty()){
          return noteService.listByTag(tag);
      }
      else{
          return Collections.emptyList();
      }
    }
    
    @GetMapping("/debug/jwt")
    public ResponseEntity<String> debugJwt(@AuthenticationPrincipal Jwt jwt) {
        StringBuilder response = new StringBuilder();
        response.append("JWT Subject: ").append(jwt.getSubject()).append("\n");
        response.append("JWT Issuer: ").append(jwt.getIssuer()).append("\n");
        response.append("All Claims: ").append(jwt.getClaims()).append("\n");
        
        // Try to get user_id from different possible claim names
        Object user_id = jwt.getClaim("user_id");
        Object sub = jwt.getClaim("sub");
        Object userId = jwt.getClaim("userId");
        
        response.append("user_id claim: ").append(user_id).append("\n");
        response.append("sub claim: ").append(sub).append("\n");
        response.append("userId claim: ").append(userId).append("\n");
        
        return ResponseEntity.ok(response.toString());
    }
    
    @GetMapping("/debug/user/{userId}")
    public ResponseEntity<String> debugUser(@PathVariable Long userId) {
        try {
            UserDto user = userClient.getById(userId);
            return ResponseEntity.ok("User found: " + user.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calling user service: " + e.getMessage());
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
