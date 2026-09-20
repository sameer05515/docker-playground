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


# V3 — Kubernetes

V3 adds Kubernetes deployment on top of V2.

## Kubernetes architecture

```text
                         Kubernetes Cluster
                                |
                         +------+------+
                         |             |
                    Frontend Pods   Backend Pods
                    (2 replicas)    (2 replicas)
                         |             |
                         |        +----+----+
                         |        |         |
                         |     MySQL Service
                         |        |
                         |     MySQL Pod
                         |        |
                         |      PVC
                         |
                    Ingress
                       |
                 todo.local:80
```

### Kubernetes resources

```text
Namespace
  |
  +-- ConfigMap
  +-- Secret
  +-- PersistentVolumeClaim
  +-- MySQL Deployment
  +-- MySQL Service
  +-- Backend Deployment (2 replicas)
  +-- Backend Service
  +-- Frontend Deployment (2 replicas)
  +-- Frontend Service
  +-- Ingress
```

## Prerequisites

For the easiest Windows learning setup:

- Docker Desktop
- Kubernetes enabled in Docker Desktop
- kubectl

Verify:

```powershell
kubectl config current-context
kubectl get nodes
```

You should see the Docker Desktop Kubernetes context and a `Ready` node.

## 1. Build Docker images

From the project root:

```powershell
.\scripts\build-images.ps1
```

Or manually:

```powershell
docker build -t todo-backend:v3 ./backend
docker build -t todo-frontend:v3 ./frontend
```

Because Docker Desktop Kubernetes uses the local Docker image store, `imagePullPolicy: IfNotPresent` allows Kubernetes to use these images without pushing them to Docker Hub.

## 2. Deploy to Kubernetes

```powershell
.\scripts\deploy-k8s.ps1
```

Or:

```powershell
kubectl apply -k ./k8s
```

Check:

```powershell
kubectl -n todo-app get pods
```

Expected:

```text
mysql-xxxxx              1/1   Running
todo-backend-xxxxx      1/1   Running
todo-backend-xxxxx      1/1   Running
todo-frontend-xxxxx     1/1   Running
todo-frontend-xxxxx     1/1   Running
```

Check services:

```powershell
kubectl -n todo-app get svc
```

Check deployments:

```powershell
kubectl -n todo-app get deployments
```

Check PVC:

```powershell
kubectl -n todo-app get pvc
```

## 3. Access the application

The easiest approach, especially while learning Kubernetes, is port-forwarding:

```powershell
kubectl -n todo-app port-forward service/todo-frontend 8088:80
```

Open:

```text
http://localhost:8088
```

This is intentionally similar to the Docker Desktop Kubernetes workflow.

The browser talks to Nginx in the frontend pod.

Nginx forwards:

```text
/api/*
```

to:

```text
todo-backend:8080
```

The backend talks to:

```text
mysql:3306
```

## 4. Inspect Kubernetes

All resources:

```powershell
kubectl -n todo-app get all
```

Detailed pods:

```powershell
kubectl -n todo-app get pods -o wide
```

Backend logs:

```powershell
kubectl -n todo-app logs deployment/todo-backend
```

MySQL logs:

```powershell
kubectl -n todo-app logs deployment/mysql
```

Frontend logs:

```powershell
kubectl -n todo-app logs deployment/todo-frontend
```

Describe a pod:

```powershell
kubectl -n todo-app describe pod <pod-name>
```

## 5. Scale backend

```powershell
kubectl -n todo-app scale deployment todo-backend --replicas=3
```

Check:

```powershell
kubectl -n todo-app get pods
```

Scale back:

```powershell
kubectl -n todo-app scale deployment todo-backend --replicas=2
```

## 6. Test self-healing

Find backend pods:

```powershell
kubectl -n todo-app get pods
```

Delete one backend pod:

```powershell
kubectl -n todo-app delete pod <backend-pod-name>
```

Then:

```powershell
kubectl -n todo-app get pods
```

Kubernetes creates a replacement because the Deployment maintains the desired replica count.

## 7. View rollout

```powershell
kubectl -n todo-app rollout status deployment/todo-backend
```

History:

```powershell
kubectl -n todo-app rollout history deployment/todo-backend
```

## 8. Delete V3

```powershell
.\scripts\delete-k8s.ps1
```

Or:

```powershell
kubectl delete -k ./k8s
```

### Important

Deleting the Kubernetes resources also deletes the PVC object. Depending on the local Kubernetes storage provisioner and reclaim behavior, persistent storage may or may not be retained.

For a clean learning reset, verify:

```powershell
kubectl get pv
kubectl -n todo-app get pvc
```

## Why Kubernetes is useful here

V2:

```text
Docker Compose
    |
    +-- frontend container
    +-- backend container
    +-- mysql container
```

V3:

```text
Kubernetes
    |
    +-- Deployment
    |     +-- frontend pod
    |     +-- frontend pod
    |
    +-- Deployment
    |     +-- backend pod
    |     +-- backend pod
    |
    +-- Deployment
          +-- mysql pod
                |
                +-- PVC
```

The important concepts to practice in this V3 are:

1. Container vs Pod
2. Pod vs Deployment
3. Service discovery
4. ClusterIP Service
5. ConfigMap
6. Secret
7. Readiness probe
8. Liveness probe
9. Replica management
10. Self-healing
11. PersistentVolumeClaim
12. Ingress
13. Port forwarding
14. Rolling deployments

## Recommended learning sequence

Run V2 first:

```powershell
docker compose up --build
```

Understand:

```text
Container
Network
Volume
Compose Service
```

Then stop V2 and run V3:

```powershell
.\scripts\build-images.ps1
.\scripts\deploy-k8s.ps1
kubectl -n todo-app port-forward service/todo-frontend 8088:80
```

Then practice:

```text
Pod
Deployment
Service
ConfigMap
Secret
PVC
Probe
Scaling
Self-healing
Ingress
```
