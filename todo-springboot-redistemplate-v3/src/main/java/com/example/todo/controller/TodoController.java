package com.example.todo.controller;
import com.example.todo.dto.*;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/todos")
public class TodoController {
 private final TodoService service;
 public TodoController(TodoService service){this.service=service;}
 @PostMapping public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));}
 @GetMapping public PageResponse<TodoResponse> search(@RequestParam(required=false) String keyword,@RequestParam(required=false) Boolean completed,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){return service.search(keyword,completed,page,size);}
 @GetMapping("/{id}") public TodoResponse get(@PathVariable Long id){return service.findById(id);}
 @PutMapping("/{id}") public TodoResponse update(@PathVariable Long id,@Valid @RequestBody TodoRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
 @GetMapping("/{id}/cache-ttl") public long ttl(@PathVariable Long id){return service.cacheTtl(id);}
 @DeleteMapping("/cache") public ResponseEntity<Void> clearCache(){service.clearCache();return ResponseEntity.noContent().build();}
}