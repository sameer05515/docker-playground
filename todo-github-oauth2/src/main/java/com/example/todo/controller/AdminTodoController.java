package com.example.todo.controller;

import com.example.todo.entity.AppUser;
import com.example.todo.entity.Role;
import com.example.todo.entity.Todo;
import com.example.todo.repository.UserRepository;
import com.example.todo.service.TodoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTodoController {

    private final TodoService todoService;
    private final UserRepository userRepository;

    public AdminTodoController(TodoService todoService, UserRepository userRepository) {
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    @GetMapping("/todos")
    public String allTodos(Model model) {
        AppUser admin = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_ADMIN)
                .findFirst()
                .orElseThrow();

        model.addAttribute("todos", todoService.findTodos(admin));
        model.addAttribute("admin", admin);

        return "admin/todos";
    }
}
