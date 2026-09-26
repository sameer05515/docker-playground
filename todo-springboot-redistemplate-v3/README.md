# Todo Spring Boot + RedisTemplate V3

MySQL is the **source of truth**. Redis is the **cache**.

## Cache-aside flow

GET:
```text
Redis HIT  -> return
Redis MISS -> MySQL -> put Redis -> return
```

UPDATE:
```text
MySQL UPDATE -> Redis PUT
```

DELETE:
```text
MySQL DELETE -> Redis EVICT
```

## Stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- MySQL 8.4
- Spring Data Redis
- RedisTemplate
- Docker Compose

## Run

```bash
docker compose up --build
```

App:
```text
http://localhost:8080
```

Infrastructure only:
```bash
docker compose up -d mysql redis
mvn clean spring-boot:run
```

## APIs

```text
POST   /api/todos
GET    /api/todos
GET    /api/todos/{id}
PUT    /api/todos/{id}
DELETE /api/todos/{id}
GET    /api/todos/{id}/cache-ttl
DELETE /api/todos/cache
```

Search/pagination:
```text
GET /api/todos?keyword=redis&completed=true&page=0&size=10
```

## Redis

```bash
docker exec -it todo-redis-v3 redis-cli
```

```redis
SCAN 0
GET todo:cache:1
TTL todo:cache:1
```

Default cache TTL is 30 minutes.

## Project architecture

```text
Controller
   |
Service
  /  /   Redis MySQL
Cache Source
```

This version demonstrates cache-aside, cache hit/miss, TTL, cache population and cache invalidation explicitly using RedisTemplate.
