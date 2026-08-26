package com.example.todo.kafka;

import com.example.todo.model.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "todo-events";

    public void sendEvent(String action, Todo todo) {
        try {
            String message = objectMapper.writeValueAsString(
                new EventMessage(action, todo)
            );
            kafkaTemplate.send(TOPIC, message);
            log.info("Event sent: {} - {}", action, todo.getId());
        } catch (Exception e) {
            log.error("Failed to send event", e);
        }
    }

    record EventMessage(String action, Todo todo) {}
}
