package com.example.todo.controller;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Todo> all(Authentication authentication) {
        return repository.findAllByOwnerId(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<Todo> create(@RequestBody Todo todo,
                                       Authentication authentication) {
        todo.setOwnerId(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id,
                                       @RequestBody Todo input,
                                       Authentication authentication) {
        return repository.findByIdAndOwnerId(id, authentication.getName())
                .map(todo -> {
                    todo.setTitle(input.getTitle());
                    todo.setCompleted(input.isCompleted());
                    return ResponseEntity.ok(repository.save(todo));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       Authentication authentication) {
        return repository.findByIdAndOwnerId(id, authentication.getName())
                .map(todo -> {
                    repository.delete(todo);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
