package com.example.todo.config;

import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(factory);
        StringRedisSerializer key = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer value = new GenericJackson2JsonRedisSerializer();
        t.setKeySerializer(key);
        t.setHashKeySerializer(key);
        t.setValueSerializer(value);
        t.setHashValueSerializer(value);
        t.afterPropertiesSet();
        return t;
    }
}