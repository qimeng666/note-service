package com.example.note_service.Dto;

import java.time.Instant;

public class NoteEvent {
    private Long noteId;
    private Long userId;
    private String title;
    private Instant timestamp;

    public NoteEvent() {
    }

    public NoteEvent(Long id, Long userId, String title, Instant now) {
        this.noteId = id;
        this.userId = userId;
        this.title = title;
        this.timestamp = now;
    }

    public Long getNoteId() {
        return this.noteId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getTitle() {
        return this.title;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NoteEvent)) return false;
        final NoteEvent other = (NoteEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$noteId = this.getNoteId();
        final Object other$noteId = other.getNoteId();
        if (this$noteId == null ? other$noteId != null : !this$noteId.equals(other$noteId)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        final Object this$timestamp = this.getTimestamp();
        final Object other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NoteEvent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $noteId = this.getNoteId();
        result = result * PRIME + ($noteId == null ? 43 : $noteId.hashCode());
        final Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final Object $timestamp = this.getTimestamp();
        result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
        return result;
    }

    public String toString() {
        return "NoteEvent(noteId=" + this.getNoteId() + ", userId=" + this.getUserId() + ", title=" + this.getTitle() + ", timestamp=" + this.getTimestamp() + ")";
    }
}
