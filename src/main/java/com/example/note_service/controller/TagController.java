package com.example.note_service.controller;

import com.example.note_service.Dto.TagRequest;
import com.example.note_service.entity.Tag;
import com.example.note_service.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        Tag created = tagService.create(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(@PathVariable Long id) {
        Tag tag = tagService.getById(id);
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Tag> getByName(@PathVariable String name) {
        Tag tag = tagService.getByName(name);
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable Long id, @RequestBody TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        Tag updated = tagService.update(id, tag);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 