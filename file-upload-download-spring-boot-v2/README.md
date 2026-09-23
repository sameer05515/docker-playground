# Spring Boot File Upload / Download V2

V2 adds MongoDB Docker and persistent file metadata.

## Architecture

```text
Browser
   |
   v
Spring Boot MVC + Thymeleaf
   |
   +--------------------+
   |                    |
   v                    v
MongoDB             Local Filesystem
(metadata)          (file bytes)
   |                    |
   +---------+----------+
             |
        docker volume
```

MongoDB runs in Docker. File bytes remain in `./uploads` on the host.

## Stack

- Java 17
- Spring Boot 3.4.5
- Spring MVC
- Thymeleaf
- Spring Data MongoDB
- MongoDB 8 Docker
- Maven

## Start MongoDB

From the project root:

```bash
docker compose up -d
```

Check:

```bash
docker ps
```

MongoDB should be available at:

```text
localhost:27017
```

## Start Spring Boot

In another terminal:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Stop MongoDB

```bash
docker compose down
```

To also remove the MongoDB data volume:

```bash
docker compose down -v
```

## Database

Database:

```text
filedb
```

Collection:

```text
files
```

Example metadata:

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

## Important design choice

V2 stores:

- File metadata -> MongoDB
- Actual file bytes -> `./uploads`

This is intentional for learning.

The application does NOT store large binary files inside MongoDB.

## API

- `GET /` - web UI
- `POST /upload` - upload file
- `GET /download/{id}` - download by MongoDB metadata ID
- `POST /delete/{id}` - delete metadata and physical file

## Production improvements for V3

- REST API
- React + Vite frontend
- JWT authentication
- Per-user files
- S3/object storage
- MIME validation
- File extension allowlist
- Virus scanning
- Pagination
- Search
- File size quotas
- Dockerize Spring Boot
- MongoDB authentication
- Health checks
