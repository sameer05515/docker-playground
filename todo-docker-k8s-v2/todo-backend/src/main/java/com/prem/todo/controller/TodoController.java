package com.prem.todo.controller;
import com.prem.todo.model.Todo;
import com.prem.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/todos") @CrossOrigin(origins="*")
public class TodoController {
 private final TodoService service;
 public TodoController(TodoService service){this.service=service;}
 @GetMapping public List<Todo> findAll(){return service.findAll();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Todo create(@Valid @RequestBody Todo todo){return service.create(todo);}
 @PutMapping("/{id}") public Todo update(@PathVariable Long id,@Valid @RequestBody Todo todo){return service.update(id,todo);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
