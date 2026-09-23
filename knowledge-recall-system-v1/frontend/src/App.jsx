import { useEffect, useMemo, useState } from 'react'
import api from './api'

const emptyForm = {
  title: '',
  question: '',
  answer: '',
  category: 'Java',
  tags: '',
  example: '',
  notes: ''
}

function App() {
  const [items, setItems] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const categories = useMemo(() => {
    return [...new Set(items.map(item => item.category).filter(Boolean))].sort()
  }, [items])

  async function loadItems() {
    setLoading(true)
    try {
      const response = await api.get('/knowledge', {
        params: {
          search: search || undefined,
          category: search ? undefined : (category || undefined)
        }
      })
      setItems(response.data)
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to load knowledge.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadItems()
  }, [search, category])

  function handleChange(event) {
    const { name, value } = event.target
    setForm(current => ({ ...current, [name]: value }))
  }

  async function saveKnowledge(event) {
    event.preventDefault()

    const payload = {
      ...form,
      tags: form.tags
        .split(',')
        .map(tag => tag.trim())
        .filter(Boolean)
    }

    try {
      if (editingId) {
        await api.put(`/knowledge/${editingId}`, payload)
        setMessage('Knowledge updated.')
      } else {
        await api.post('/knowledge', payload)
        setMessage('Knowledge added.')
      }

      resetForm()
      await loadItems()
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to save knowledge.')
    }
  }

  function editKnowledge(item) {
    setEditingId(item.id)
    setForm({
      title: item.title || '',
      question: item.question || '',
      answer: item.answer || '',
      category: item.category || '',
      tags: (item.tags || []).join(', '),
      example: item.example || '',
      notes: item.notes || ''
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  async function deleteKnowledge(id) {
    if (!window.confirm('Delete this knowledge item?')) return

    try {
      await api.delete(`/knowledge/${id}`)
      setMessage('Knowledge deleted.')
      await loadItems()
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to delete knowledge.')
    }
  }

  function resetForm() {
    setEditingId(null)
    setForm(emptyForm)
  }

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">PERSONAL LEARNING SYSTEM</p>
          <h1>🧠 Knowledge Recall</h1>
          <p className="subtitle">
            Add knowledge today. Recall it tomorrow. Build interview confidence.
          </p>
        </div>
        <div className="stats">
          <span>{items.length}</span>
          <small>visible cards</small>
        </div>
      </header>

      <main>
        <section className="panel">
          <div className="section-title">
            <div>
              <h2>{editingId ? 'Edit Knowledge' : 'Add Knowledge'}</h2>
              <p>V1 focuses on organizing your knowledge.</p>
            </div>
            {editingId && <button className="secondary" onClick={resetForm}>Cancel</button>}
          </div>

          <form onSubmit={saveKnowledge} className="form-grid">
            <label>
              Title
              <input name="title" value={form.title} onChange={handleChange}
                     placeholder="HashMap Internal Working" required />
            </label>

            <label>
              Category
              <input name="category" value={form.category} onChange={handleChange}
                     placeholder="Java" />
            </label>

            <label className="full">
              Question
              <textarea name="question" value={form.question} onChange={handleChange}
                        placeholder="How does HashMap work internally?" required />
            </label>

            <label className="full">
              Answer
              <textarea name="answer" value={form.answer} onChange={handleChange}
                        placeholder="Write your interview-ready answer..." required />
            </label>

            <label>
              Tags
              <input name="tags" value={form.tags} onChange={handleChange}
                     placeholder="java, collections, hashmap" />
            </label>

            <label>
              Example
              <input name="example" value={form.example} onChange={handleChange}
                     placeholder="Map<String,Integer> map = new HashMap<>();" />
            </label>

            <label className="full">
              Notes / Memory Trick
              <textarea name="notes" value={form.notes} onChange={handleChange}
                        placeholder="Bucket → hash → collision → tree/list" />
            </label>

            <div className="actions full">
              <button className="primary" type="submit">
                {editingId ? 'Update Knowledge' : 'Add Knowledge'}
              </button>
              {!editingId && <button type="button" className="secondary" onClick={resetForm}>Clear</button>}
            </div>
          </form>
        </section>

        <section className="toolbar">
          <div>
            <h2>Knowledge Explorer</h2>
            <p>Search and review what you have stored.</p>
          </div>

          <div className="filters">
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="🔍 Search..."
            />
            <select value={category} onChange={e => setCategory(e.target.value)}>
              <option value="">All categories</option>
              {categories.map(value => <option key={value} value={value}>{value}</option>)}
            </select>
          </div>
        </section>

        {message && <div className="message">{message}</div>}

        {loading ? (
          <div className="empty">Loading...</div>
        ) : items.length === 0 ? (
          <div className="empty">
            <div className="empty-icon">🧠</div>
            <h3>No knowledge cards yet</h3>
            <p>Add your first interview concept above.</p>
          </div>
        ) : (
          <section className="cards">
            {items.map(item => (
              <article className="card" key={item.id}>
                <div className="card-top">
                  <span className="category">{item.category || 'General'}</span>
                  <span className="date">
                    {item.updatedAt ? new Date(item.updatedAt).toLocaleDateString() : ''}
                  </span>
                </div>

                <h3>{item.title}</h3>
                <div className="question">Q: {item.question}</div>

                <details>
                  <summary>Read answer</summary>
                  <p className="answer">{item.answer}</p>
                  {item.example && (
                    <pre><code>{item.example}</code></pre>
                  )}
                  {item.notes && (
                    <div className="notes"><strong>Memory:</strong> {item.notes}</div>
                  )}
                </details>

                {item.tags?.length > 0 && (
                  <div className="tags">
                    {item.tags.map(tag => <span key={tag}>#{tag}</span>)}
                  </div>
                )}

                <div className="card-actions">
                  <button className="secondary" onClick={() => editKnowledge(item)}>Edit</button>
                  <button className="danger" onClick={() => deleteKnowledge(item.id)}>Delete</button>
                </div>
              </article>
            ))}
          </section>
        )}
      </main>
    </div>
  )
}

export default App
