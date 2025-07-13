package com.example.note_service.service.impl;

import com.example.note_service.entity.Tag;
import com.example.note_service.repository.TagRepository;
import com.example.note_service.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag create(Tag tag) {
        // 检查标签名是否已存在
        if (tagRepository.findByName(tag.getName()).isPresent()) {
            throw new RuntimeException("Tag with name '" + tag.getName() + "' already exists");
        }
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    @Override
    public Tag getByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));
    }

    @Override
    public Tag update(Long id, Tag tag) {
        Tag existingTag = getById(id);
        
        // 检查新名称是否与其他标签冲突
        if (!existingTag.getName().equals(tag.getName())) {
            if (tagRepository.findByName(tag.getName()).isPresent()) {
                throw new RuntimeException("Tag with name '" + tag.getName() + "' already exists");
            }
        }
        
        existingTag.setName(tag.getName());
        return tagRepository.save(existingTag);
    }

    @Override
    public void delete(Long id) {
        Tag tag = getById(id);
        tagRepository.delete(tag);
    }
} 