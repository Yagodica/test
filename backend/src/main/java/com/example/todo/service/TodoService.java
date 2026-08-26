package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.kafka.TodoEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository repository;
    private final TodoEventProducer eventProducer;

    public List<Todo> findAll() {
        return repository.findAll();
    }

    public Todo save(Todo todo) {
        Todo saved = repository.save(todo);
        eventProducer.sendEvent("CREATED", saved);
        return saved;
    }

    public void delete(Long id) {
        Todo todo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        repository.deleteById(id);
        eventProducer.sendEvent("DELETED", todo);
    }
}
