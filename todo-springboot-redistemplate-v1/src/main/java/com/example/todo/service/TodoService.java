package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRedisRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoService {

    private final TodoRedisRepository repository;

    private final AtomicLong idGenerator = new AtomicLong(0);

    public TodoService(TodoRedisRepository repository) {
        this.repository = repository;
    }

    public TodoResponse create(TodoRequest request) {

        Long id = nextId();

        Todo todo = Todo.builder()
                .id(id)
                .title(request.title())
                .description(request.description())
                .completed(Boolean.TRUE.equals(request.completed()))
                .build();

        return toResponse(repository.save(todo));
    }

    public List<TodoResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TodoResponse findById(Long id) {

        Todo todo = repository.findById(id);

        if (todo == null) {
            throw new TodoNotFoundException(id);
        }

        return toResponse(todo);
    }

    public TodoResponse update(Long id, TodoRequest request) {

        Todo existing = repository.findById(id);

        if (existing == null) {
            throw new TodoNotFoundException(id);
        }

        Todo updated = Todo.builder()
                .id(id)
                .title(request.title())
                .description(request.description())
                .completed(Boolean.TRUE.equals(request.completed()))
                .build();

        return toResponse(repository.save(updated));
    }

    public void delete(Long id) {

        if (repository.findById(id) == null) {
            throw new TodoNotFoundException(id);
        }

        repository.deleteById(id);
    }

    private Long nextId() {

        List<Todo> todos = repository.findAll();

        long maxId = todos.stream()
                .mapToLong(Todo::getId)
                .max()
                .orElse(0);

        return idGenerator.updateAndGet(
                current -> Math.max(current + 1, maxId + 1)
        );
    }

    private TodoResponse toResponse(Todo todo) {

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCompleted()
        );
    }
}
