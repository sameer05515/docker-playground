$ErrorActionPreference = "Stop"

kubectl apply -k ./k8s

Write-Host ""
Write-Host "Waiting for deployments..."
kubectl -n todo-app rollout status deployment/mysql --timeout=180s
kubectl -n todo-app rollout status deployment/todo-backend --timeout=180s
kubectl -n todo-app rollout status deployment/todo-frontend --timeout=180s

Write-Host ""
kubectl -n todo-app get pods
kubectl -n todo-app get svc
