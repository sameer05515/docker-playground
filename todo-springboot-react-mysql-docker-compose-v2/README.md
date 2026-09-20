# Todo App V2 — Kubernetes

Spring Boot + React/Vite + MySQL, now deployable to Kubernetes.

## Architecture

```text
Browser
   |
   v
todo-frontend Service :30080
   |
   v
Frontend Pods (2)
   |
   | HTTP -> localhost:8080 from browser
   v
todo-backend Service :8080
   |
   v
Backend Pods (2)
   |
   v
mysql Service :3306
   |
   v
MySQL Pod
   |
   v
PersistentVolumeClaim
```

> In a real production setup, the frontend would normally use an ingress/reverse proxy so the browser does not need to know the backend address. This V2 intentionally keeps the architecture simple for learning Kubernetes.

## Prerequisites

- Docker Desktop
- Kubernetes enabled in Docker Desktop
- kubectl

Verify:

```bash
kubectl version --client
kubectl get nodes
```

## Build images

From project root:

```bash
docker build -t todo-backend:v2 ./backend
docker build -t todo-frontend:v2 ./frontend
```

Docker Desktop Kubernetes can use these locally built images with:

```yaml
imagePullPolicy: IfNotPresent
```

## Deploy

```bash
kubectl apply -f k8s/
```

Check:

```bash
kubectl get all -n todo-app
kubectl get pods -n todo-app
kubectl get svc -n todo-app
kubectl get pvc -n todo-app
```

Wait until all pods are Running/Ready:

```bash
kubectl get pods -n todo-app -w
```

## Open the application

Frontend:

```text
http://localhost:30080
```

Backend:

```text
http://localhost:8080/api/todos
```

For Docker Desktop Kubernetes, NodePort 30080 is normally available on localhost.

## Useful Kubernetes commands

```bash
kubectl get pods -n todo-app
kubectl describe pod -n todo-app <pod-name>
kubectl logs -n todo-app deployment/todo-backend
kubectl logs -n todo-app deployment/mysql
kubectl get svc -n todo-app
kubectl get pvc -n todo-app
```

Scale backend:

```bash
kubectl scale deployment todo-backend --replicas=3 -n todo-app
```

Scale frontend:

```bash
kubectl scale deployment todo-frontend --replicas=3 -n todo-app
```

## Delete the application

```bash
kubectl delete namespace todo-app
```

This removes the namespace and its Kubernetes resources, including the PVC.

## Important Kubernetes concepts demonstrated

- Namespace
- Pod
- Deployment
- Replica count
- Service
- ClusterIP
- NodePort
- Secret
- PersistentVolumeClaim
- Readiness probe
- Liveness probe
- Service-to-service DNS
- Container image
- Scaling

## Docker Compose vs Kubernetes

V1:

```text
docker compose
   |
   +-- mysql container
   +-- backend container
   +-- frontend container
```

V2:

```text
Kubernetes
   |
   +-- mysql Deployment
   |      |
   |      +-- MySQL Pod
   |      +-- PVC
   |
   +-- backend Deployment
   |      |
   |      +-- Backend Pod
   |      +-- Backend Pod
   |
   +-- frontend Deployment
          |
          +-- Frontend Pod
          +-- Frontend Pod
```

The main learning point is that Kubernetes manages Pods/Deployments/Services rather than treating a Docker Compose file as the deployment model.
