package com.example.todo.repository;

import com.example.todo.model.Todo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TodoRedisRepository {

    private static final String TODO_KEY_PREFIX = "todo:";
    private static final String TODO_IDS_KEY = "todo:ids";

    private final RedisTemplate<String, Object> redisTemplate;

    public TodoRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Todo save(Todo todo) {

        String key = key(todo.getId());

        redisTemplate.opsForHash().put(key, "id", todo.getId().toString());
        redisTemplate.opsForHash().put(key, "title", todo.getTitle());
        redisTemplate.opsForHash().put(
                key,
                "description",
                Objects.requireNonNullElse(todo.getDescription(), "")
        );
        redisTemplate.opsForHash().put(
                key,
                "completed",
                Boolean.toString(Boolean.TRUE.equals(todo.getCompleted()))
        );

        redisTemplate.opsForSet().add(
                TODO_IDS_KEY,
                todo.getId().toString()
        );

        return todo;
    }

    public Todo findById(Long id) {

        String key = key(id);

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return null;
        }

        return readTodo(key);
    }

    public List<Todo> findAll() {

        var ids = redisTemplate.opsForSet().members(TODO_IDS_KEY);

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Todo> todos = new ArrayList<>();

        for (Object id : ids) {
            Todo todo = findById(Long.valueOf(id.toString()));

            if (todo != null) {
                todos.add(todo);
            }
        }

        todos.sort((a, b) -> Long.compare(a.getId(), b.getId()));

        return todos;
    }

    public void deleteById(Long id) {

        redisTemplate.delete(key(id));

        redisTemplate.opsForSet().remove(
                TODO_IDS_KEY,
                id.toString()
        );
    }

    private Todo readTodo(String key) {

        var values = redisTemplate.opsForHash().entries(key);

        return Todo.builder()
                .id(Long.valueOf(values.get("id").toString()))
                .title((String) values.get("title"))
                .description((String) values.get("description"))
                .completed(Boolean.parseBoolean(
                        values.get("completed").toString()
                ))
                .build();
    }

    private String key(Long id) {
        return TODO_KEY_PREFIX + id;
    }
}
