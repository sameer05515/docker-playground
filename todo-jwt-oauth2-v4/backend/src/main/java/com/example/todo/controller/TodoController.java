package com.example.todo.controller;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private String ownerId(HttpServletRequest request) {
        return (String) request.getAttribute("todoOwnerId");
    }

    @GetMapping
    public List<Todo> getTodos(Authentication authentication,
                               HttpServletRequest request) {
        if (isAdmin(authentication)) {
            return repository.findAll();
        }
        return repository.findAllByOwnerId(ownerId(request));
    }

    @PostMapping
    public ResponseEntity<Todo> create(@RequestBody Todo todo,
                                       HttpServletRequest request) {
        todo.setId(null);
        todo.setOwnerId(ownerId(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id,
                                       @RequestBody Todo input,
                                       HttpServletRequest request) {
        return repository.findByIdAndOwnerId(id, ownerId(request))
                .map(todo -> {
                    todo.setTitle(input.getTitle());
                    todo.setCompleted(input.isCompleted());
                    return ResponseEntity.ok(repository.save(todo));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       HttpServletRequest request) {
        return repository.findByIdAndOwnerId(id, ownerId(request))
                .map(todo -> {
                    repository.delete(todo);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
