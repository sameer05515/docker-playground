import { useEffect, useState } from 'react'

const API = 'http://localhost:8080/api/todos'

function App() {
  const [todos, setTodos] = useState([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [loading, setLoading] = useState(false)

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

    setLoading(true)
    await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title,
        description,
        completed: false
      })
    })

    setTitle('')
    setDescription('')
    await loadTodos()
    setLoading(false)
  }

  const toggleTodo = async (todo) => {
    await fetch(`${API}/${todo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: todo.title,
        description: todo.description,
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
    <main className="container">
      <h1>Todo App</h1>

      <form onSubmit={addTodo} className="todo-form">
        <input
          value={title}
          onChange={e => setTitle(e.target.value)}
          placeholder="Todo title"
        />
        <input
          value={description}
          onChange={e => setDescription(e.target.value)}
          placeholder="Description"
        />
        <button disabled={loading}>Add Todo</button>
      </form>

      <section className="todos">
        {todos.map(todo => (
          <article className={`todo ${todo.completed ? 'completed' : ''}`} key={todo.id}>
            <div>
              <h3>{todo.title}</h3>
              <p>{todo.description}</p>
            </div>

            <div className="actions">
              <button onClick={() => toggleTodo(todo)}>
                {todo.completed ? 'Undo' : 'Complete'}
              </button>
              <button className="delete" onClick={() => deleteTodo(todo.id)}>
                Delete
              </button>
            </div>
          </article>
        ))}

        {!todos.length && <p className="empty">No todos yet.</p>}
      </section>
    </main>
  )
}

export default App
