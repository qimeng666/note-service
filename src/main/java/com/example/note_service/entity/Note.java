package com.example.note_service.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "note_tag",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    public Note() {
    }

    public Long getId() {
        return this.id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Note)) return false;
        final Note other = (Note) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Note;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    public String toString() {
        return "Note(id=" + this.getId() + ", userId=" + this.getUserId() + ", title=" + this.getTitle() + ", content=" + this.getContent() + ")";
    }
}
