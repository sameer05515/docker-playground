package com.example.todo.todo;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoRepository repo;
    public TodoController(TodoRepository repo){this.repo=repo;}

    @GetMapping public List<Todo> all(){return repo.findAll();}
    @PostMapping public Todo create(@RequestBody Todo todo){return repo.save(todo);}
    @PutMapping("/{id}") public Todo update(@PathVariable Long id,@RequestBody Todo todo){
        Todo t=repo.findById(id).orElseThrow();
        t.setTitle(todo.getTitle()); t.setCompleted(todo.isCompleted());
        return repo.save(t);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
}
