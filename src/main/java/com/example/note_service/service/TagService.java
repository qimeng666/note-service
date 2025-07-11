package com.example.note_service.service;

import com.example.note_service.entity.Tag;
import java.util.List;

public interface TagService {
    Tag create(Tag tag);
    List<Tag> getAllTags();
    Tag getById(Long id);
    Tag getByName(String name);
    Tag update(Long id, Tag tag);
    void delete(Long id);
} 