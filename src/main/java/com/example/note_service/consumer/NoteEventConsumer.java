package com.example.note_service.consumer;

import com.example.note_service.Dto.NoteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoteEventConsumer {
    @KafkaListener(topics = "note.events", groupId = "notes-event-consumer")
    public void consume(NoteEvent event) {
        log.info("[NoteEventConsumer] Received event: noteId={}, userId={}, title='{}', time={}",
                event.getNoteId(), event.getUserId(), event.getTitle(), event.getTimestamp());
    }
}
