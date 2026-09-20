import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  headers: {
    "Content-Type": "application/json"
  }
});

export const getTodos = () => api.get("/todos");
export const createTodo = todo => api.post("/todos", todo);
export const updateTodo = (id, todo) => api.put(`/todos/${id}`, todo);
export const toggleTodo = id => api.patch(`/todos/${id}/toggle`);
export const deleteTodo = id => api.delete(`/todos/${id}`);
