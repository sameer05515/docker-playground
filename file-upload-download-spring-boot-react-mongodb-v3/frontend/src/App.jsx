import { useEffect, useState } from 'react'
import { deleteFile, downloadUrl, getFiles, uploadFile } from './api'

function formatBytes(bytes) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

function App() {
  const [files, setFiles] = useState([])
  const [search, setSearch] = useState('')
  const [selectedFile, setSelectedFile] = useState(null)
  const [uploadProgress, setUploadProgress] = useState(0)
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  async function loadFiles(term = search) {
    try {
      setLoading(true)
      setError('')
      setFiles(await getFiles(term))
    } catch (e) {
      setError(e.response?.data?.error || 'Could not load files.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadFiles('')
  }, [])

  async function handleUpload(event) {
    event.preventDefault()
    if (!selectedFile) return

    try {
      setMessage('')
      setError('')
      setUploadProgress(0)

      await uploadFile(selectedFile, setUploadProgress)

      setMessage('File uploaded successfully.')
      setSelectedFile(null)
      event.target.reset()
      await loadFiles()
    } catch (e) {
      setError(e.response?.data?.error || 'Upload failed.')
    } finally {
      setUploadProgress(0)
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('Delete this file?')) return

    try {
      setError('')
      await deleteFile(id)
      setMessage('File deleted successfully.')
      await loadFiles()
    } catch (e) {
      setError(e.response?.data?.error || 'Delete failed.')
    }
  }

  function handleSearch(event) {
    event.preventDefault()
    loadFiles(search)
  }

  function clearSearch() {
    setSearch('')
    loadFiles('')
  }

  return (
    <div className="page">
      <header className="hero">
        <div>
          <p className="eyebrow">SPRING BOOT + REACT + MONGODB</p>
          <h1>File Manager V3</h1>
          <p className="subtitle">
            Upload, search, download and delete files through a REST API.
          </p>
        </div>
        <div className="badge">V3</div>
      </header>

      {message && <div className="alert success">{message}</div>}
      {error && <div className="alert error">{error}</div>}

      <section className="card">
        <h2>Upload file</h2>
        <form className="upload-form" onSubmit={handleUpload}>
          <input
            type="file"
            onChange={e => setSelectedFile(e.target.files?.[0] || null)}
            required
          />
          <button disabled={!selectedFile || uploadProgress > 0}>
            {uploadProgress > 0 ? `Uploading ${uploadProgress}%` : 'Upload'}
          </button>
        </form>
        <p className="hint">Maximum file size: 20 MB</p>
      </section>

      <section className="card">
        <div className="section-header">
          <div>
            <h2>Files</h2>
            <p className="hint">{files.length} file(s)</p>
          </div>
          <form className="search" onSubmit={handleSearch}>
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search by file name..."
            />
            <button type="submit">Search</button>
            <button type="button" className="secondary" onClick={clearSearch}>
              Clear
            </button>
          </form>
        </div>

        {loading ? (
          <div className="empty">Loading...</div>
        ) : files.length === 0 ? (
          <div className="empty">No files found.</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>File</th>
                  <th>Type</th>
                  <th>Size</th>
                  <th>Uploaded</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {files.map(file => (
                  <tr key={file.id}>
                    <td>
                      <strong>{file.originalFileName}</strong>
                    </td>
                    <td>{file.contentType}</td>
                    <td>{formatBytes(file.size)}</td>
                    <td>{new Date(file.uploadedAt).toLocaleString()}</td>
                    <td className="actions">
                      <a
                        className="button"
                        href={downloadUrl(file.id)}
                        target="_blank"
                        rel="noreferrer"
                      >
                        Download
                      </a>
                      <button
                        className="danger"
                        onClick={() => handleDelete(file.id)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <footer>
        <span>Backend: Spring Boot REST API</span>
        <span>Database: MongoDB Docker</span>
        <span>Storage: Local filesystem</span>
      </footer>
    </div>
  )
}

export default App
