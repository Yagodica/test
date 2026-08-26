package com.example.todo.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PageController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${backend.url:http://todo-backend:8080}")
    private String backendUrl;

    @GetMapping("/")
    public String index(Model model) {
        try {
            ResponseEntity<Todo[]> response = restTemplate.getForEntity(
                    backendUrl + "/api/todos", Todo[].class);
            List<Todo> todos = Arrays.asList(response.getBody());
            model.addAttribute("todos", todos);
        } catch (Exception e) {
            log.error("Failed to fetch todos", e);
            model.addAttribute("error", "Не удалось загрузить задачи");
        }
        return "index";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title) {
        try {
            Todo todo = new Todo(title);
            restTemplate.postForEntity(backendUrl + "/api/todos", todo, Todo.class);
        } catch (Exception e) {
            log.error("Failed to add todo", e);
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        try {
            restTemplate.delete(backendUrl + "/api/todos/" + id);
        } catch (Exception e) {
            log.error("Failed to delete todo", e);
        }
        return "redirect:/";
    }

    // DTO для работы с API
    record Todo(Long id, String title, boolean completed) {
        Todo(String title) {
            this(null, title, false);
        }
    }
}
