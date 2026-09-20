package com.example.todo.controller;
import com.example.todo.entity.Todo; import com.example.todo.repository.TodoRepository; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/todos") public class TodoController{
 private final TodoRepository repository; public TodoController(TodoRepository r){repository=r;}
 @GetMapping public List<Todo> all(){return repository.findAll();}
 @PostMapping public ResponseEntity<Todo> create(@RequestBody Todo t){return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(t));}
 @PutMapping("/{id}") public ResponseEntity<Todo> update(@PathVariable Long id,@RequestBody Todo in){return repository.findById(id).map(t->{t.setTitle(in.getTitle());t.setCompleted(in.isCompleted());return ResponseEntity.ok(repository.save(t));}).orElseGet(()->ResponseEntity.notFound().build());}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){if(!repository.existsById(id))return ResponseEntity.notFound().build();repository.deleteById(id);return ResponseEntity.noContent().build();}
}