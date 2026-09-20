# Todo Docker + Kubernetes V2

## Stack
Java 17 + Spring Boot + PostgreSQL + React/Vite + Nginx + Docker Compose + basic Kubernetes.

## What changed from V1
- H2 -> PostgreSQL
- PostgreSQL container + persistent named volume
- Environment-based DB configuration
- Spring Boot Actuator health endpoint
- Docker health checks
- `depends_on` waits for healthy dependencies
- Multi-stage Maven Docker build
- `.env.example`
- Kubernetes namespace, PostgreSQL, backend and frontend manifests

## Run with Docker Compose

From this directory:

```powershell
docker compose up --build
```

Open:

```text
http://localhost:3000
```

Check:

```powershell
docker ps
docker compose logs
docker compose logs backend
docker compose logs postgres
```

Stop:

```powershell
docker compose down
```

Delete database volume too:

```powershell
docker compose down -v
```

## Important Docker networking concept

Inside Compose:

```text
frontend -> backend:8080
backend  -> postgres:5432
```

Do not use `localhost` between containers. Docker Compose service names become DNS names.

## Environment

Optional:

```powershell
copy .env.example .env
```

`.env`:

```text
POSTGRES_DB=tododb
POSTGRES_USER=todo
POSTGRES_PASSWORD=todo123
POSTGRES_PORT=5432
```

## Kubernetes learning

First make Docker Compose work.

Then enable Kubernetes in Docker Desktop and check:

```powershell
kubectl version --client
```

Apply:

```powershell
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
```

Check:

```powershell
kubectl get pods -n todo-app
kubectl get svc -n todo-app
```

Kubernetes concepts introduced:

```text
Namespace
   ↓
Deployment
   ↓
Pod
   ↓
Service
```

## Recommended learning order

V1: Docker basics + two containers

V2: PostgreSQL + Compose + volume + environment variables + networking + health checks

V3: Kubernetes Pod + Deployment + Service + ConfigMap + Secret + persistent storage

V4: probes + replicas + scaling + rolling updates

V5: Ingress + resource limits + production-style configuration
