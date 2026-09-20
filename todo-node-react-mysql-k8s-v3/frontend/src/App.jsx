import { useEffect, useMemo, useState } from "react";
import TodoForm from "./components/TodoForm";
import TodoList from "./components/TodoList";
import {
  getTodos,
  createTodo,
  updateTodo,
  toggleTodo,
  deleteTodo
} from "./api/todoApi";

function App() {
  const [todos, setTodos] = useState([]);
  const [filter, setFilter] = useState("all");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadTodos = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getTodos();
      setTodos(response.data);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to connect to backend.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTodos();
  }, []);

  const handleAdd = async todo => {
    try {
      const response = await createTodo(todo);
      setTodos(current => [response.data, ...current]);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to add todo.");
    }
  };

  const handleToggle = async id => {
    try {
      const response = await toggleTodo(id);

      setTodos(current =>
        current.map(todo => todo.id === id ? response.data : todo)
      );
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update todo.");
    }
  };

  const handleUpdate = async (id, data) => {
    try {
      const response = await updateTodo(id, data);

      setTodos(current =>
        current.map(todo => todo.id === id ? response.data : todo)
      );
    } catch (err) {
      setError(err.response?.data?.message || "Failed to edit todo.");
    }
  };

  const handleDelete = async id => {
    try {
      await deleteTodo(id);
      setTodos(current => current.filter(todo => todo.id !== id));
    } catch (err) {
      setError(err.response?.data?.message || "Failed to delete todo.");
    }
  };

  const filteredTodos = useMemo(() => {
    if (filter === "active") return todos.filter(todo => !todo.completed);
    if (filter === "completed") return todos.filter(todo => todo.completed);
    return todos;
  }, [todos, filter]);

  const completedCount = todos.filter(todo => todo.completed).length;

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Todo App V2</h1>
          <p>Node.js + React + MySQL + Docker Compose</p>
        </div>

        <div className="stats">
          <span>{todos.length} total</span>
          <span>{completedCount} completed</span>
        </div>
      </header>

      <main className="container">
        <TodoForm onAdd={handleAdd} />

        {error && (
          <div className="error">
            {error}
            <button onClick={() => setError("")}>×</button>
          </div>
        )}

        <div className="toolbar">
          <div className="filters">
            {["all", "active", "completed"].map(item => (
              <button
                key={item}
                className={filter === item ? "active" : ""}
                onClick={() => setFilter(item)}
              >
                {item[0].toUpperCase() + item.slice(1)}
              </button>
            ))}
          </div>

          <button onClick={loadTodos}>Refresh</button>
        </div>

        {loading ? (
          <div className="empty">Loading...</div>
        ) : (
          <TodoList
            todos={filteredTodos}
            onToggle={handleToggle}
            onDelete={handleDelete}
            onUpdate={handleUpdate}
          />
        )}
      </main>
    </div>
  );
}

export default App;
