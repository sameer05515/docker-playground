package com.example.todo.repository;

import com.example.todo.entity.AppUser;
import com.example.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByCreatedByOrderByIdDesc(AppUser user);

    Optional<Todo> findByIdAndCreatedBy(Long id, AppUser user);
}
