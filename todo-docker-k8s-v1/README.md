# Todo Application - Docker & Kubernetes Learning Project V1

Beginner-friendly full-stack Todo application.

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 database
- React 19
- Vite
- Docker
- Docker Compose
- Basic Kubernetes manifests

## 1. Run without Docker

### Backend

```bash
cd todo-backend
mvn spring-boot:run
```

Backend API:
`http://localhost:8080/api/todos`

### Frontend

Open another terminal:

```bash
cd todo-frontend
npm install
npm run dev
```

Frontend:
`http://localhost:5173`

## 2. Run with Docker Compose

Install Docker Desktop.

From the project root:

```bash
docker compose up --build
```

Open:

`http://localhost:3000`

Stop:

```bash
docker compose down
```

Remove containers and database volume:

```bash
docker compose down -v
```

Useful Docker commands:

```bash
docker ps
docker images
docker compose logs
docker compose logs backend
docker compose logs frontend
```

## Architecture

Browser
    |
    v
React + Nginx :3000
    |
    | /api/*
    v
Spring Boot :8080
    |
    v
H2 database volume

## 3. Kubernetes

The `k8s/` directory contains simple Deployment + Service manifests.

Learning sequence:

1. Docker image
2. Container
3. Docker Compose
4. Kubernetes Pod
5. Kubernetes Deployment
6. Kubernetes Service
7. ConfigMap / Secret
8. Persistent Volume
9. Ingress
10. Scaling

## V2 plan

The next version can introduce PostgreSQL, environment variables, health checks, proper Kubernetes configuration, persistent storage, and scaling.
