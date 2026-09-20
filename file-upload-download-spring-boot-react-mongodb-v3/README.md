# File Upload / Download V3

Full-stack file management application:

- Spring Boot REST API
- React + Vite frontend
- MongoDB in Docker
- Local filesystem for actual file bytes
- MongoDB for file metadata
- Upload, list, download and delete
- Search files
- File size and MIME type display
- CORS configured for Vite
- UUID-based stored filenames
- Basic path traversal protection

## Architecture

```text
React + Vite
     |
     | REST / multipart
     v
Spring Boot REST API
     |
     +----------------------+
     |                      |
     v                      v
MongoDB                  ./uploads
(metadata)               (file bytes)
     |
     v
Docker volume
```

## Run MongoDB

```powershell
docker compose up -d
```

Check:

```powershell
docker ps
```

MongoDB:
`localhost:27017`

## Run Backend

```powershell
cd backend
mvn spring-boot:run
```

Backend:
`http://localhost:8080`

Health:
`http://localhost:8080/api/health`

## Run Frontend

Open a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

Frontend:
`http://localhost:5173`

## API

### Health

```http
GET /api/health
```

### List

```http
GET /api/files
GET /api/files?search=resume
```

### Upload

```http
POST /api/files
Content-Type: multipart/form-data
file=<file>
```

### Download

```http
GET /api/files/{id}/download
```

### Delete

```http
DELETE /api/files/{id}
```

## Storage model

MongoDB stores metadata:

```json
{
  "_id": "...",
  "originalFileName": "resume.pdf",
  "storedFileName": "uuid_resume.pdf",
  "contentType": "application/pdf",
  "size": 123456,
  "uploadedAt": "2026-09-23T10:00:00Z"
}
```

Actual bytes are stored in:

```text
./uploads/
```

## Configuration

Backend:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/filedb
file.storage.location=./uploads
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

Frontend:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

## V3 learning goals

This version separates:

1. React UI
2. REST API
3. Business/service layer
4. MongoDB repository
5. Physical file storage

## Possible V4

- JWT authentication
- User-specific files
- Role-based authorization
- S3/MinIO object storage
- Pagination
- File preview
- Image/PDF preview
- Upload progress
- Dockerize backend and frontend
- Nginx reverse proxy
- Docker Compose full stack
- MongoDB authentication
