package com.example.todo.repository;
import com.example.todo.entity.Todo;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
public interface TodoRepository extends JpaRepository<Todo,Long> {
 @Query("SELECT t FROM Todo t WHERE (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:keyword,'%')) OR LOWER(COALESCE(t.description,'')) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND (:completed IS NULL OR t.completed=:completed)")
 Page<Todo> search(String keyword,Boolean completed,Pageable pageable);
}