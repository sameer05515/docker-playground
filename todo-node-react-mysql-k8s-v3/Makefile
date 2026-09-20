build:
	docker build -t todo-backend:v3 ./backend
	docker build -t todo-frontend:v3 ./frontend

k8s-up:
	kubectl apply -k ./k8s

k8s-status:
	kubectl -n todo-app get all
	kubectl -n todo-app get pvc

k8s-down:
	kubectl delete -k ./k8s
