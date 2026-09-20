# Todo App V2 — Node.js + React + MySQL + Docker Compose

Full-stack Todo application with:

- Node.js + Express backend
- React + Vite frontend
- MySQL database
- Docker
- Docker Compose
- Persistent MySQL volume
- REST API
- Health checks
- Environment configuration

## Architecture

```text
Browser
   |
   v
React + Vite
   |
   | HTTP /api
   v
Node.js + Express
   |
   | MySQL protocol
   v
MySQL
```

## Project structure

```text
todo-node-react-mysql-v2/
├── backend/
│   ├── src/
│   │   ├── db/
│   │   │   └── mysql.js
│   │   ├── routes/
│   │   │   └── todoRoutes.js
│   │   └── server.js
│   ├── Dockerfile
│   ├── package.json
│   └── .env.example
├── frontend/
│   ├── src/
│   │   ├── api/todoApi.js
│   │   ├── components/
│   │   │   ├── TodoForm.jsx
│   │   │   ├── TodoItem.jsx
│   │   │   └── TodoList.jsx
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.js
├── mysql/
│   └── init/
│       └── 01-schema.sql
├── docker-compose.yml
├── .env.example
└── .gitignore
```

# Option 1 — Run everything with Docker Compose

Requirements:

- Docker Desktop

From the project root:

```powershell
docker compose up --build
```

Open:

```text
http://localhost:5173
```

Backend API:

```text
http://localhost:8080/api/todos
```

Health:

```text
http://localhost:8080/api/health
```

Stop:

```powershell
docker compose down
```

Stop and delete database data:

```powershell
docker compose down -v
```

The MySQL data is stored in the Docker named volume:

```text
todo_mysql_data
```

## Check containers

```powershell
docker compose ps
```

## View logs

```powershell
docker compose logs -f
```

Backend only:

```powershell
docker compose logs -f backend
```

MySQL only:

```powershell
docker compose logs -f mysql
```

# Option 2 — Run backend/frontend locally

Start MySQL using Docker:

```powershell
docker compose up -d mysql
```

Backend:

```powershell
cd backend
npm install
npm run dev
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

## Database

Database:

```text
todo_db
```

Table:

```text
todos
```

Schema:

```sql
id
title
description
completed
created_at
updated_at
```

## REST API

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/todos` | Get all todos |
| GET | `/api/todos/:id` | Get todo |
| POST | `/api/todos` | Create todo |
| PUT | `/api/todos/:id` | Update todo |
| PATCH | `/api/todos/:id/toggle` | Toggle completion |
| DELETE | `/api/todos/:id` | Delete todo |

Create:

```json
{
  "title": "Learn Docker",
  "description": "Learn Docker Compose"
}
```

## Important Docker concepts in this version

### 1. Containers

Three containers are used:

```text
frontend
backend
mysql
```

### 2. Docker network

Compose creates a network so containers can communicate.

The backend connects to MySQL using:

```text
mysql
```

NOT:

```text
localhost
```

Inside the backend container:

```text
mysql:3306
```

means the MySQL service.

### 3. MySQL volume

MySQL data survives container recreation because of:

```yaml
volumes:
  - mysql_data:/var/lib/mysql
```

### 4. Startup dependency

Backend depends on MySQL health:

```yaml
depends_on:
  mysql:
    condition: service_healthy
```

The backend also retries the MySQL connection.

# Useful commands

Build:

```powershell
docker compose build
```

Start:

```powershell
docker compose up
```

Start detached:

```powershell
docker compose up -d
```

Stop:

```powershell
docker compose down
```

Rebuild:

```powershell
docker compose up --build
```

Check images:

```powershell
docker images
```

Check containers:

```powershell
docker ps
```

Check volumes:

```powershell
docker volume ls
```

Enter MySQL:

```powershell
docker compose exec mysql mysql -u todo_user -ptodo_password todo_db
```

Then:

```sql
SHOW TABLES;
SELECT * FROM todos;
```
