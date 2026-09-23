import axios from 'axios'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export const api = axios.create({
  baseURL: API_BASE_URL,
})

export async function getFiles(search = '') {
  const response = await api.get('/files', {
    params: search ? { search } : {},
  })
  return response.data
}

export async function uploadFile(file, onProgress) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await api.post('/files', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: event => {
      if (!event.total) return
      onProgress?.(Math.round((event.loaded * 100) / event.total))
    },
  })

  return response.data
}

export async function deleteFile(id) {
  await api.delete(`/files/${id}`)
}

export function downloadUrl(id) {
  return `${API_BASE_URL}/files/${id}/download`
}
