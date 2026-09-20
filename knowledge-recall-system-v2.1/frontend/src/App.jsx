import { useEffect, useMemo, useRef, useState } from 'react'
import api from './api'
import ReactMarkdown from 'react-markdown'

const emptyForm = {
  title: '',
  question: '',
  answer: '',
  category: 'Java',
  tags: '',
  example: '',
  notes: ''
}

const ratings = [
  { key: 'AGAIN', label: '❌ Again', help: 'Review tomorrow' },
  { key: 'HARD', label: '😐 Hard', help: 'Review in 2 days' },
  { key: 'GOOD', label: '🙂 Good', help: 'Review in 5 days' },
  { key: 'EASY', label: '😎 Easy', help: 'Review in 10 days' }
]

function App() {
  const [items, setItems] = useState([])
  const [due, setDue] = useState([])
  const [stats, setStats] = useState({})
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [tab, setTab] = useState('dashboard')
  const [recallIndex, setRecallIndex] = useState(0)
  const [revealed, setRevealed] = useState(false)
  const [attempt, setAttempt] = useState('')
  const [message, setMessage] = useState('')
  const importInputRef = useRef(null)

  async function loadAll() {
    try {
      const [knowledge, dueItems, dashboard] = await Promise.all([
        api.get('/knowledge', {
          params: {
            search: search || undefined,
            category: search ? undefined : (category || undefined)
          }
        }),
        api.get('/recall/due'),
        api.get('/recall/stats')
      ])
      setItems(knowledge.data)
      setDue(dueItems.data)
      setStats(dashboard.data)
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to load data.')
    }
  }

  useEffect(() => {
    loadAll()
  }, [search, category])

  const categories = useMemo(
    () => [...new Set(items.map(item => item.category).filter(Boolean))].sort(),
    [items]
  )

  const current = due[recallIndex]

  function handleChange(e) {
    setForm(current => ({ ...current, [e.target.name]: e.target.value }))
  }

  async function saveKnowledge(e) {
    e.preventDefault()

    const payload = {
      ...form,
      tags: form.tags.split(',').map(x => x.trim()).filter(Boolean)
    }

    try {
      if (editingId) {
        await api.put(`/knowledge/${editingId}`, payload)
        setMessage('Knowledge updated.')
      } else {
        await api.post('/knowledge', payload)
        setMessage('Knowledge added. It is now due for recall.')
      }
      resetForm()
      await loadAll()
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to save.')
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
    setTab('knowledge')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  async function deleteKnowledge(id) {
    if (!confirm('Delete this knowledge item?')) return
    await api.delete(`/knowledge/${id}`)
    setMessage('Knowledge deleted.')
    await loadAll()
  }

  function resetForm() {
    setEditingId(null)
    setForm(emptyForm)
  }

  async function rate(rating) {
    if (!current) return

    try {
      await api.post(`/recall/${current.id}/review`, { rating })
      setRevealed(false)
      setAttempt('')
      if (recallIndex >= due.length - 1) {
        await loadAll()
        setRecallIndex(0)
      } else {
        setRecallIndex(i => i + 1)
        await loadAll()
      }
      setMessage(`Marked ${rating.toLowerCase()}. Next review scheduled.`)
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to save review.')
    }
  }

  async function exportKnowledge() {
    try {
      const response = await api.get('/knowledge')

      // V2.1 export contract intentionally uses the application/API `id`.
      // Spring Data maps this Java id to MongoDB's internal `_id`.
      const exportData = {
        format: 'knowledge-recall',
        version: 2,
        exportedAt: new Date().toISOString(),
        items: response.data
      }

      const blob = new Blob([JSON.stringify(exportData, null, 2)], {
        type: 'application/json'
      })

      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `knowledge-recall-export-${new Date().toISOString().slice(0, 10)}.json`
      document.body.appendChild(link)
      link.click()
      link.remove()
      URL.revokeObjectURL(url)

      setMessage(`Exported ${exportData.items.length} knowledge items.`)
    } catch (error) {
      setMessage(error.response?.data?.error || 'Unable to export knowledge.')
    }
  }

  function openImportDialog() {
    importInputRef.current?.click()
  }

  async function importKnowledge(event) {
    const file = event.target.files?.[0]
    event.target.value = ''

    if (!file) return

    try {
      const text = await file.text()
      const parsed = JSON.parse(text)
      const importedItems = Array.isArray(parsed) ? parsed : parsed.items

      if (!Array.isArray(importedItems)) {
        throw new Error(
          'Invalid import file. Expected a JSON array or an object containing "items".'
        )
      }

      const response = await api.get('/knowledge')
      const existingIds = new Set(response.data.map(item => item.id))

      const toImport = []
      let skipped = 0
      let overwritten = 0

      for (const rawItem of importedItems) {
        if (!rawItem || typeof rawItem !== 'object') continue

        // V2.1 uses ONLY `id` for import identity.
        const importedId = rawItem.id
        const item = { ...rawItem }

        if (importedId && existingIds.has(importedId)) {
          const overwrite = window.confirm(
            `Knowledge with id "${importedId}" already exists.\n\n` +
            `OK = Overwrite existing record\n` +
            `Cancel = Skip this record`
          )

          if (!overwrite) {
            skipped++
            continue
          }

          overwritten++
        }

        toImport.push(item)
      }

      if (toImport.length === 0) {
        setMessage(`Import completed. ${skipped} item(s) skipped.`)
        return
      }

      await api.post('/knowledge/import', toImport)
      await loadAll()

      setMessage(
        `Import completed: ${toImport.length} imported, ` +
        `${overwritten} overwritten, ${skipped} skipped.`
      )
    } catch (error) {
      setMessage(
        error.response?.data?.error ||
        error.message ||
        'Unable to import knowledge.'
      )
    }
  }

  function startRecall() {
    setRecallIndex(0)
    setRevealed(false)
    setAttempt('')
    setTab('recall')
  }

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">PERSONAL LEARNING SYSTEM · V2</p>
          <h1>🧠 Knowledge Recall</h1>
          <p className="subtitle">
            Don't just collect information. Recall it until you can explain it.
          </p>
        </div>
        <div className="due-box">
          <strong>{stats.due ?? 0}</strong>
          <span>due today</span>
        </div>
      </header>

      <nav className="nav">
        <button className={tab === 'dashboard' ? 'active' : ''} onClick={() => setTab('dashboard')}>Dashboard</button>
        <button className={tab === 'recall' ? 'active' : ''} onClick={startRecall}>🧠 Recall</button>
        <button className={tab === 'knowledge' ? 'active' : ''} onClick={() => setTab('knowledge')}>📚 Knowledge</button>
      </nav>

      <main>
        {message && <div className="message">{message}</div>}

        {tab === 'dashboard' && (
          <>
            <section className="dashboard-grid">
              <div className="metric"><span>{stats.total ?? 0}</span><small>Total knowledge</small></div>
              <div className="metric"><span>{stats.due ?? 0}</span><small>Due for recall</small></div>
              <div className="metric"><span>{stats.accuracy ?? 0}%</span><small>Recall accuracy</small></div>
              <div className="metric"><span>{stats.weakTopics ?? 0}</span><small>Weak topics</small></div>
            </section>

            <section className="welcome panel">
              <div>
                <p className="eyebrow dark">THE 4R METHOD</p>
                <h2>Read → Recall → Relate → Respond</h2>
                <p>
                  V2 introduces the Recall Engine. First think about the answer,
                  then reveal it and rate your performance.
                </p>
              </div>
              <button className="primary big" onClick={startRecall}>
                Start Recall · {stats.due ?? 0}
              </button>
            </section>

            <section className="panel">
              <h2>How the Recall Engine works</h2>
              <div className="flow">
                <span>Question</span><b>→</b><span>Think</span><b>→</b><span>Reveal</span><b>→</b><span>Rate</span><b>→</b><span>Schedule</span>
              </div>
            </section>
          </>
        )}

        {tab === 'recall' && (
          <section className="recall-page">
            {!current ? (
              <div className="empty panel">
                <div className="empty-icon">🎉</div>
                <h2>No recall due right now</h2>
                <p>Add more knowledge or come back when your next review is due.</p>
                <button className="primary" onClick={() => setTab('knowledge')}>Add Knowledge</button>
              </div>
            ) : (
              <div className="recall-card">
                <div className="recall-meta">
                  <span>{current.category || 'General'}</span>
                  <small>{recallIndex + 1} / {due.length}</small>
                </div>

                <h2>{current.title}</h2>
                <div className="recall-question">{current.question}</div>

                <label className="attempt">
                  Your answer
                  <textarea
                    value={attempt}
                    onChange={e => setAttempt(e.target.value)}
                    placeholder="Try to explain it without looking at the answer..."
                  />
                </label>

                {!revealed ? (
                  <button className="primary reveal" onClick={() => setRevealed(true)}>
                    👀 Reveal Expected Answer
                  </button>
                ) : (
                  <>
                    <div className="expected">
                      <h3>Expected answer</h3>
                      <div className="markdown"><ReactMarkdown>{current.answer}</ReactMarkdown></div>
                      {current.example && <pre><code>{current.example}</code></pre>}
                      {current.notes && <div className="notes"><strong>Memory:</strong><div className="markdown"><ReactMarkdown>{current.notes}</ReactMarkdown></div></div>}
                    </div>

                    <div className="rating">
                      <h3>How did you perform?</h3>
                      <div className="rating-grid">
                        {ratings.map(rating => (
                          <button key={rating.key} onClick={() => rate(rating.key)}>
                            <strong>{rating.label}</strong>
                            <small>{rating.help}</small>
                          </button>
                        ))}
                      </div>
                    </div>
                  </>
                )}
              </div>
            )}
          </section>
        )}

        {tab === 'knowledge' && (
          <>
            <section className="panel">
              <div className="section-title">
                <div>
                  <h2>{editingId ? 'Edit Knowledge' : 'Add Knowledge'}</h2>
                  <p>Every new card automatically becomes due for recall.</p>
                </div>
                <div className="header-actions">
                  <button className="secondary" type="button" onClick={exportKnowledge}>⬇ Export JSON</button>
                  <button className="secondary" type="button" onClick={openImportDialog}>⬆ Import JSON</button>
                  <input ref={importInputRef} type="file" accept=".json,application/json" onChange={importKnowledge} hidden />
                  {editingId && <button className="secondary" onClick={resetForm}>Cancel</button>}
                </div>
              </div>

              <form onSubmit={saveKnowledge} className="form-grid">
                <label>Title
                  <input name="title" value={form.title} onChange={handleChange} placeholder="HashMap Internal Working" required />
                </label>
                <label>Category
                  <input name="category" value={form.category} onChange={handleChange} placeholder="Java" />
                </label>
                <label className="full">Question
                  <textarea name="question" value={form.question} onChange={handleChange} placeholder="How does HashMap work internally?" required />
                </label>
                <label className="full">Answer
                  <textarea name="answer" value={form.answer} onChange={handleChange} placeholder="Write your interview-ready answer..." required />
                </label>
                <label>Tags
                  <input name="tags" value={form.tags} onChange={handleChange} placeholder="java, collections, hashmap" />
                </label>
                <label>Example
                  <input name="example" value={form.example} onChange={handleChange} placeholder="Map<String,Integer> map = new HashMap<>();" />
                </label>
                <label className="full">Notes / Memory Trick
                  <textarea name="notes" value={form.notes} onChange={handleChange} placeholder="Bucket → hash → collision → tree/list" />
                </label>
                <div className="actions full">
                  <button className="primary" type="submit">{editingId ? 'Update Knowledge' : 'Add Knowledge'}</button>
                  {!editingId && <button type="button" className="secondary" onClick={resetForm}>Clear</button>}
                </div>
              </form>
            </section>

            <section className="toolbar">
              <div><h2>Knowledge Explorer</h2><p>Search, read and maintain your cards.</p></div>
              <div className="filters">
                <input value={search} onChange={e => setSearch(e.target.value)} placeholder="🔍 Search..." />
                <select value={category} onChange={e => setCategory(e.target.value)}>
                  <option value="">All categories</option>
                  {categories.map(value => <option key={value} value={value}>{value}</option>)}
                </select>
              </div>
            </section>

            <section className="cards">
              {items.map(item => (
                <article className="card" key={item.id}>
                  <div className="card-top">
                    <span className="category">{item.category || 'General'}</span>
                    <span className="confidence">{item.confidence ?? 0}% confidence</span>
                  </div>
                  <h3>{item.title}</h3>
                  <div className="question"><strong>Q:</strong><div className="markdown"><ReactMarkdown>{item.question}</ReactMarkdown></div></div>
                  <details>
                    <summary>Read answer</summary>
                    <div className="answer markdown"><ReactMarkdown>{item.answer}</ReactMarkdown></div>
                    {item.example && <pre><code>{item.example}</code></pre>}
                    {item.notes && <div className="notes"><strong>Memory:</strong><div className="markdown"><ReactMarkdown>{item.notes}</ReactMarkdown></div></div>}
                  </details>
                  <div className="review-info">
                    Reviews: {item.reviewCount ?? 0} · Correct: {item.correctCount ?? 0}
                    {item.nextReviewAt && <> · Next: {new Date(item.nextReviewAt).toLocaleDateString()}</>}
                  </div>
                  <div className="card-actions">
                    <button className="secondary" onClick={() => editKnowledge(item)}>Edit</button>
                    <button className="danger" onClick={() => deleteKnowledge(item.id)}>Delete</button>
                  </div>
                </article>
              ))}
            </section>
          </>
        )}
      </main>
    </div>
  )
}

export default App
