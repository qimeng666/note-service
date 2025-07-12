package com.example.note_service.Dto;

import java.util.Set;

public class NoteRequest {
//    private Long userId; // User ID to associate the note with
    private String title;
    private String content;
    private Set<String> tags; // Comma-separated tag names

    public NoteRequest() {
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Set<String> getTags() {
        return this.tags;
    }

//    public Long getUserId() {
//        return this.userId;
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NoteRequest)) return false;
        final NoteRequest other = (NoteRequest) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        final Object this$content = this.getContent();
        final Object other$content = other.getContent();
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
        final Object this$tags = this.getTags();
        final Object other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NoteRequest;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final Object $content = this.getContent();
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        final Object $tags = this.getTags();
        result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
        return result;
    }

    public String toString() {
        return "NoteRequest(title=" + this.getTitle() + ", content=" + this.getContent() + ", tags=" + this.getTags() + ")";
    }

//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
}
