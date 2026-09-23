# Interview Preparation Roadmap — Node.js + React + Vite + MongoDB V1

This project converts the supplied Alpine.js interview roadmap into a full-stack application.

## Stack
- Node.js + Express
- MongoDB + Mongoose
- React + Vite
- Axios
- Docker Compose
- Nginx production frontend

## Features
- Roadmap phases/topics imported from the supplied ZIP
- Search and filters
- Topic status: Not started → In progress → Completed
- Topic detail modal
- Interview questions
- Personal notes persisted in MongoDB
- Daily routine persisted in MongoDB
- Today's focus
- Study timer
- 10-question mock interview
- Mock score persistence
- Export progress JSON
- MongoDB Docker volume

## 1. MongoDB Docker + local Node/React

```powershell
docker compose up -d mongodb
```

Backend:
```powershell
cd backend
copy .env.example .env
npm install
npm run seed
npm run dev
```

Frontend in another terminal:
```powershell
cd frontend
copy .env.example .env
npm install
npm run dev
```

Open http://localhost:5173

## 2. Full Docker

```powershell
docker compose up --build
```

Open http://localhost:3000

API health:
http://localhost:8080/api/health

Stop:
```powershell
docker compose down
```

MongoDB data remains in the `mongodb_data` Docker volume.
