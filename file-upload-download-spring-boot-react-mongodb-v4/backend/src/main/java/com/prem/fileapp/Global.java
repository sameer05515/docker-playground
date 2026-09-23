package com.prem.fileapp;import org.springframework.web.bind.annotation.*;import org.springframework.http.*;import java.util.*;
@RestControllerAdvice public class Global{@ExceptionHandler(IllegalArgumentException.class)ResponseEntity<Map<String,String>> bad(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}}
