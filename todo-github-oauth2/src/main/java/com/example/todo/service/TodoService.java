package com.example.todo.service;

import com.example.todo.entity.AppUser;
import com.example.todo.entity.Role;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<Todo> findTodos(AppUser user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return todoRepository.findAll();
        }
        return todoRepository.findByCreatedByOrderByIdDesc(user);
    }

    public Todo create(Todo todo, AppUser user) {
        todo.setId(null);
        todo.setCreatedBy(user);
        return todoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public Todo findForEdit(Long id, AppUser user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return getOrThrow(id);
        }

        return todoRepository.findByIdAndCreatedBy(id, user)
                .orElseThrow(() -> new AccessDeniedException("You cannot access this todo"));
    }

    public void update(Long id, Todo formTodo, AppUser user) {
        Todo todo = findForEdit(id, user);

        todo.setTitle(formTodo.getTitle());
        todo.setDescription(formTodo.getDescription());
        todo.setCompleted(formTodo.isCompleted());

        todoRepository.save(todo);
    }

    public void delete(Long id, AppUser user) {
        Todo todo = findForEdit(id, user);
        todoRepository.delete(todo);
    }

    private Todo getOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));
    }
}
