# Todo App — Spring Boot + React/Vite + MySQL + Docker Compose

## Architecture

Browser → React/Vite → Spring Boot REST API → MySQL

MySQL runs inside Docker Compose. The backend and frontend can run locally for development.

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+
- Docker Desktop

## 1. Start MySQL

```bash
docker compose up -d mysql
docker compose ps
```

MySQL:
- host: `localhost`
- port: `3307`
- database: `todo_db`
- username: `todo_user`
- password: `todo_password`

The database is persisted in the Docker volume `mysql_data`.

## 2. Run Spring Boot

```bash
cd backend
mvn spring-boot:run
```

API: http://localhost:8080/api/todos

## 3. Run React/Vite

```bash
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173

## 4. Run everything with Docker Compose

This project also contains Dockerfiles for the backend and frontend.

```bash
docker compose up --build
```

Then open:

http://localhost:5173

## API

```text
GET    /api/todos
GET    /api/todos/{id}
POST   /api/todos
PUT    /api/todos/{id}
DELETE /api/todos/{id}
```

Example:

```json
{
  "title": "Learn Docker",
  "description": "Understand containers and images",
  "completed": false
}
```

## Useful Docker commands

```bash
docker compose ps
docker compose logs -f mysql
docker compose logs -f backend
docker compose down
docker compose down -v
```

`docker compose down -v` also removes the MySQL data volume.
