package com.example.todo.service;

import com.example.todo.dto.PageResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoService {

    private final TodoRedisRepository repository;
    private final AtomicLong idGenerator = new AtomicLong();

    private final long ttlSeconds;

    public TodoService(
            TodoRedisRepository repository,
            @Value("${app.redis.todo-ttl-minutes:30}") long ttlMinutes) {

        this.repository = repository;
        this.ttlSeconds = ttlMinutes * 60;
    }

    public TodoResponse create(TodoRequest request) {

        Todo todo = Todo.builder()
                .id(nextId())
                .title(request.title())
                .description(request.description())
                .completed(Boolean.TRUE.equals(request.completed()))
                .build();

        repository.save(todo);
        repository.refreshTtl(todo.getId(), ttlSeconds);

        return toResponse(todo);
    }

    public TodoResponse findById(Long id) {

        Todo todo = repository.findById(id);

        if (todo == null) {
            throw new TodoNotFoundException(id);
        }

        return toResponse(todo);
    }

    public PageResponse<TodoResponse> search(
            String keyword,
            Boolean completed,
            int page,
            int size) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        final String normalizedKeyword =
                keyword == null ? "" : keyword.trim().toLowerCase();

        List<Todo> filtered = repository.findAll()
                .stream()
                .filter(todo -> matchesKeyword(todo, normalizedKeyword))
                .filter(todo -> completed == null ||
                        Boolean.TRUE.equals(todo.getCompleted()) == completed)
                .toList();

        long total = filtered.size();

        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());

        List<TodoResponse> content = filtered.subList(from, to)
                .stream()
                .map(this::toResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(
                content,
                page,
                size,
                total,
                totalPages,
                page == 0,
                totalPages == 0 || page >= totalPages - 1
        );
    }

    public TodoResponse update(Long id, TodoRequest request) {

        if (repository.findById(id) == null) {
            throw new TodoNotFoundException(id);
        }

        Todo todo = Todo.builder()
                .id(id)
                .title(request.title())
                .description(request.description())
                .completed(Boolean.TRUE.equals(request.completed()))
                .build();

        repository.save(todo);
        repository.refreshTtl(id, ttlSeconds);

        return toResponse(todo);
    }

    public void delete(Long id) {

        if (repository.findById(id) == null) {
            throw new TodoNotFoundException(id);
        }

        repository.deleteById(id);
    }

    public long count() {
        Long count = repository.getKeyCount();
        return count == null ? 0 : count;
    }

    private boolean matchesKeyword(Todo todo, String keyword) {

        if (keyword.isBlank()) {
            return true;
        }

        return contains(todo.getTitle(), keyword)
                || contains(todo.getDescription(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null &&
                value.toLowerCase().contains(keyword);
    }

    private Long nextId() {

        List<Todo> todos = repository.findAll();

        long max = todos.stream()
                .mapToLong(Todo::getId)
                .max()
                .orElse(0);

        return idGenerator.updateAndGet(
                current -> Math.max(current + 1, max + 1));
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCompleted());
    }
}
