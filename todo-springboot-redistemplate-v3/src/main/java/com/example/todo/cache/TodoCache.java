package com.example.todo.cache;

import com.example.todo.dto.TodoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class TodoCache {
    private static final String PREFIX = "todo:cache:";
    private final RedisTemplate<String, Object> redis;
    private final Duration ttl;

    public TodoCache(RedisTemplate<String, Object> redis, @Value("${app.cache.todo-ttl-minutes:30}") long minutes) {
        this.redis = redis;
        this.ttl = Duration.ofMinutes(minutes);
    }

    public void put(TodoResponse todo) {
        redis.opsForValue().set(key(todo.id()), todo, ttl);
    }

    public TodoResponse get(Long id) {
        Object v = redis.opsForValue().get(key(id));
        if (v instanceof TodoResponse r)
            return r;
        return null;
    }

    public void evict(Long id) {
        redis.delete(key(id));
    }

    public long ttl(Long id) {
        Long v = redis.getExpire(key(id));
        return v == null ? -1 : v;
    }

    public void clear() {
        var keys = redis.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty())
            redis.delete(keys);
    }

    private String key(Long id) {
        return PREFIX + id;
    }
}