# Todo Spring Boot + RedisTemplate V2

## Features

- Spring Boot 3.5
- Java 17
- RedisTemplate
- Redis Hash
- Redis Set
- TTL
- Search
- Completed filter
- Pagination
- Global exception handling
- Validation
- Docker Compose
- Dockerized Spring Boot application
- Redis AOF persistence

## Architecture

```text
Client
  |
  v
TodoController
  |
  v
TodoService
  |
  v
TodoRedisRepository
  |
  v
RedisTemplate
  |
  v
Redis
```

## Redis keys

```text
todo:1
todo:2
todo:3

todo:ids
```

Each Todo is represented as a Redis Hash.

Example:

```text
todo:1

id          = 1
title       = Learn RedisTemplate
description = Practice Redis
completed   = false
```

`todo:ids` is a Redis Set containing IDs.

## TTL

New and updated Todo keys receive a configurable TTL.

Default:

```yaml
app:
  redis:
    todo-ttl-minutes: 30
```

This means the Todo hash expires after 30 minutes unless updated.

## Run locally

Start Redis:

```bash
docker compose up -d redis
```

Run Spring Boot:

```bash
mvn clean spring-boot:run
```

## Run everything in Docker

```bash
docker compose up --build
```

Application:

```text
http://localhost:8080
```

Redis:

```text
localhost:6379
```

## API

```text
POST   /api/todos
GET    /api/todos
GET    /api/todos/{id}
PUT    /api/todos/{id}
DELETE /api/todos/{id}
GET    /api/todos/stats/count
```

### Pagination

```http
GET /api/todos?page=0&size=10
```

### Search

```http
GET /api/todos?keyword=redis
```

### Completed filter

```http
GET /api/todos?completed=true
```

### Combined

```http
GET /api/todos?keyword=learn&completed=false&page=0&size=10
```

## Redis inspection

```bash
docker exec -it todo-redis-v2 redis-cli
```

```redis
SCAN 0
SMEMBERS todo:ids
HGETALL todo:1
TTL todo:1
```

## Important note

This V2 intentionally demonstrates `RedisTemplate` directly rather than hiding Redis behind Spring Cache annotations.

For a larger production system, the next improvement would be replacing the in-memory ID generation and Set-based full scan with a persistent database or Redis atomic counter + sorted/indexed structures.
