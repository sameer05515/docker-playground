# Knowledge Recall System V1

A simple personal knowledge management application for interview preparation.

## Stack

- Backend: Java 17, Spring Boot 3.4.5, Spring Web, Spring Data MongoDB
- Database: MongoDB 8 running in Docker
- Frontend: React + Vite
- API: REST

## V1 Features

- Add knowledge cards
- Edit knowledge cards
- Delete knowledge cards
- View knowledge cards
- Search by question/title/tags
- Filter by category
- Store answer, example, notes and tags
- MongoDB Docker setup

## Architecture

React + Vite
      |
      | REST / JSON
      v
Spring Boot
      |
      v
MongoDB
      ^
      |
   Docker

## Run

### 1. Start MongoDB

```powershell
docker compose up -d
```

Check:

```powershell
docker ps
```

### 2. Start backend

```powershell
cd backend
mvn spring-boot:run
```

Backend:

http://localhost:8080

Health:

http://localhost:8080/api/health

### 3. Start frontend

Open another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Frontend:

http://localhost:5173

## API

### Get all knowledge

GET `/api/knowledge`

### Search

GET `/api/knowledge?search=hashmap`

### Filter

GET `/api/knowledge?category=Java`

### Get one

GET `/api/knowledge/{id}`

### Create

POST `/api/knowledge`

Example:

```json
{
  "title": "HashMap Internal Working",
  "question": "How does HashMap work internally?",
  "answer": "HashMap uses an array of buckets and handles collisions using linked nodes and tree bins under certain conditions.",
  "category": "Java",
  "tags": ["java", "collections", "hashmap"],
  "example": "Map<String, Integer> map = new HashMap<>();",
  "notes": "Remember hash, bucket, collision, resize and equals/hashCode."
}
```

### Update

PUT `/api/knowledge/{id}`

### Delete

DELETE `/api/knowledge/{id}`

## V1 scope

This version intentionally focuses on CRUD and knowledge organization.

V2 will add the Recall Engine:
- due reviews
- Again / Hard / Good / Easy
- review scheduling
- recall history
