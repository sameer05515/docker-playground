package com.prem.todo.service;

import com.prem.todo.model.Todo;
import com.prem.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public List<Todo> findAll() {
        return repository.findAll();
    }

    public Todo create(Todo todo) {
        todo.setCompleted(false);
        return repository.save(todo);
    }

    public Todo update(Long id, Todo request) {
        Todo todo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found: " + id));

        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());
        return repository.save(todo);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
