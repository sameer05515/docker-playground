package com.example.todo.dto;
import jakarta.validation.constraints.*;
public record TodoRequest(
 @NotBlank(message="Title is required") @Size(max=200) String title,
 @Size(max=1000) String description,
 Boolean completed) {}