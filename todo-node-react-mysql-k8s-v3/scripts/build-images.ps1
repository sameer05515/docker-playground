$ErrorActionPreference = "Stop"

Write-Host "Building backend image..."
docker build -t todo-backend:v3 ./backend

Write-Host "Building frontend image..."
docker build -t todo-frontend:v3 ./frontend

Write-Host ""
Write-Host "Images built:"
docker images todo-backend:v3
docker images todo-frontend:v3
