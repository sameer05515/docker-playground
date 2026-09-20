#!/usr/bin/env bash
set -e
docker build -t todo-backend:v2 ./backend
docker build -t todo-frontend:v2 ./frontend
kubectl apply -f k8s/
kubectl get pods -n todo-app
kubectl get svc -n todo-app
