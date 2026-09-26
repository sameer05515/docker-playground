package com.example.todo.exception;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(TodoNotFoundException.class)
 ResponseEntity<Map<String,Object>> notFound(TodoNotFoundException e){return body(HttpStatus.NOT_FOUND,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class)
 ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){
  Map<String,String> errors=new HashMap<>(); e.getBindingResult().getFieldErrors().forEach(x->errors.put(x.getField(),x.getDefaultMessage()));
  Map<String,Object> b=new HashMap<>(); b.put("timestamp",Instant.now());b.put("status",400);b.put("error","Validation Failed");b.put("errors",errors);
  return ResponseEntity.badRequest().body(b);
 }
 private ResponseEntity<Map<String,Object>> body(HttpStatus s,String m){Map<String,Object>b=new HashMap<>();b.put("timestamp",Instant.now());b.put("status",s.value());b.put("error",s.getReasonPhrase());b.put("message",m);return ResponseEntity.status(s).body(b);}
}