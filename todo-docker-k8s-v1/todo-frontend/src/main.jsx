import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

const API = '/api/todos'

function App() {
  const [todos, setTodos] = useState([])
  const [title, setTitle] = useState('')

  const loadTodos = async () => {
    const response = await fetch(API)
    setTodos(await response.json())
  }

  useEffect(() => {
    loadTodos()
  }, [])

  const addTodo = async (e) => {
    e.preventDefault()
    if (!title.trim()) return

    await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: title.trim() })
    })

    setTitle('')
    loadTodos()
  }

  const toggleTodo = async (todo) => {
    await fetch(`${API}/${todo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: todo.title,
        completed: !todo.completed
      })
    })
    loadTodos()
  }

  const deleteTodo = async (id) => {
    await fetch(`${API}/${id}`, { method: 'DELETE' })
    loadTodos()
  }

  return (
    <div className="container">
      <h1>Todo Application</h1>

      <form onSubmit={addTodo} className="form">
        <input
          value={title}
          onChange={e => setTitle(e.target.value)}
          placeholder="Enter todo..."
        />
        <button>Add</button>
      </form>

      <div className="list">
        {todos.map(todo => (
          <div className="todo" key={todo.id}>
            <label>
              <input
                type="checkbox"
                checked={todo.completed}
                onChange={() => toggleTodo(todo)}
              />
              <span className={todo.completed ? 'completed' : ''}>
                {todo.title}
              </span>
            </label>
            <button className="delete" onClick={() => deleteTodo(todo.id)}>
              Delete
            </button>
          </div>
        ))}

        {todos.length === 0 && <p>No todos yet.</p>}
      </div>
    </div>
  )
}

createRoot(document.getElementById('root')).render(<App />)
