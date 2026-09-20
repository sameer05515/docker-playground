package com.prem.todo.controller;

import com.prem.todo.entity.Todo;
import com.prem.todo.repository.TodoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:5173")
public class TodoController {

    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Todo> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Todo create(@Valid @RequestBody TodoRequest request) {
        return repository.save(Todo.builder()
                .title(request.title())
                .description(request.description())
                .completed(false)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request) {

        return repository.findById(id)
                .map(todo -> {
                    todo.setTitle(request.title());
                    todo.setDescription(request.description());
                    todo.setCompleted(request.completed());
                    return ResponseEntity.ok(repository.save(todo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record TodoRequest(
            @NotBlank String title,
            String description,
            boolean completed
    ) {
    }
}
