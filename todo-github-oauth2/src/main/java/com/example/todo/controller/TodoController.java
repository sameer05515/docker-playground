package com.example.todo.controller;

import com.example.todo.entity.AppUser;
import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/todos")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @GetMapping
    public String list(
            @AuthenticationPrincipal OAuth2User oauthUser,
            Model model) {

        AppUser user = userService.getCurrentUser(oauthUser);

        model.addAttribute("user", user);
        model.addAttribute("todos", todoService.findTodos(user));

        return "todos/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "todos/form";
    }

    @PostMapping
    public String create(
            @ModelAttribute Todo todo,
            @AuthenticationPrincipal OAuth2User oauthUser) {

        AppUser user = userService.getCurrentUser(oauthUser);
        todoService.create(todo, user);

        return "redirect:/todos";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauthUser,
            Model model) {

        AppUser user = userService.getCurrentUser(oauthUser);

        model.addAttribute("todo", todoService.findForEdit(id, user));

        return "todos/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute Todo todo,
            @AuthenticationPrincipal OAuth2User oauthUser) {

        AppUser user = userService.getCurrentUser(oauthUser);

        todoService.update(id, todo, user);

        return "redirect:/todos";
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauthUser) {

        AppUser user = userService.getCurrentUser(oauthUser);

        todoService.delete(id, user);

        return "redirect:/todos";
    }
}
