# Todo Spring Boot + RedisTemplate

A simple production-style Todo REST API using:

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data Redis
- `RedisTemplate`
- Bean Validation
- Docker Compose for Redis

## 1. Start Redis

```bash
docker compose up -d
```

Check:

```bash
docker ps
```

Optional Redis CLI test:

```bash
docker exec -it todo-redis redis-cli ping
```

Expected:

```text
PONG
```

## 2. Run the application

```bash
mvn clean spring-boot:run
```

Application:

```text
http://localhost:8080
```

## 3. API

### Create

```http
POST /api/todos
Content-Type: application/json

{
  "title": "Learn RedisTemplate",
  "description": "Build a Todo application",
  "completed": false
}
```

### Get all

```http
GET /api/todos
```

### Get by ID

```http
GET /api/todos/{id}
```

### Update

```http
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Learn RedisTemplate deeply",
  "description": "Practice Redis operations",
  "completed": true
}
```

### Delete

```http
DELETE /api/todos/{id}
```

## Redis data model

Each Todo is stored as a Redis hash:

```text
todo:{id}
```

The application also maintains:

```text
todo:ids
```

as a Redis Set containing all Todo IDs.

The repository uses `RedisTemplate<String, Object>` directly.

## Project structure

```text
src/main/java/com/example/todo
├── TodoApplication.java
├── config
│   └── RedisConfig.java
├── controller
│   └── TodoController.java
├── dto
│   ├── TodoRequest.java
│   └── TodoResponse.java
├── exception
│   ├── GlobalExceptionHandler.java
│   └── TodoNotFoundException.java
├── model
│   └── Todo.java
├── repository
│   └── TodoRedisRepository.java
└── service
    └── TodoService.java
```

## Useful Redis commands

```bash
docker exec -it todo-redis redis-cli

KEYS *
SMEMBERS todo:ids
HGETALL todo:1
DEL todo:1
```

For production, replace `KEYS *` with `SCAN`.
