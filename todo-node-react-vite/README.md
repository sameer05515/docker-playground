# Todo App — Node.js + Express + React + Vite

A simple full-stack Todo application.

## Stack

### Backend
- Node.js
- Express
- CORS
- In-memory Todo store (no database required)

### Frontend
- React
- Vite
- Axios
- CSS

## Project structure

```text
todo-node-react-vite/
├── backend/
│   ├── src/
│   │   ├── data/todoStore.js
│   │   ├── routes/todoRoutes.js
│   │   └── server.js
│   ├── package.json
│   └── .env.example
├── frontend/
│   ├── src/
│   │   ├── components/TodoForm.jsx
│   │   ├── components/TodoItem.jsx
│   │   ├── components/TodoList.jsx
│   │   ├── api/todoApi.js
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── package.json
│   ├── vite.config.js
│   └── .env.example
└── README.md
```

## Requirements

- Node.js 20+
- npm

## 1. Start backend

```bash
cd backend
npm install
npm run dev
```

Backend:

```text
http://localhost:8080
```

Health check:

```text
GET http://localhost:8080/api/health
```

## 2. Start frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

## API

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/todos` | Get all todos |
| GET | `/api/todos/:id` | Get one todo |
| POST | `/api/todos` | Create todo |
| PUT | `/api/todos/:id` | Update todo |
| PATCH | `/api/todos/:id/toggle` | Toggle completion |
| DELETE | `/api/todos/:id` | Delete todo |

Example:

```json
POST /api/todos

{
  "title": "Learn Node.js",
  "description": "Build a REST API"
}
```

## Important

The backend currently uses an in-memory store. Restarting Node.js clears the todos.

This version is intentionally simple so Docker/Kubernetes can be added in the next version without hiding the fundamentals.
