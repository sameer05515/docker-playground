# File Upload/Download V4

Spring Boot REST + React/Vite + MongoDB Docker.

V4 adds:
- Spring Security
- JWT access token (15 minutes)
- HttpOnly refresh-token cookie (7 days)
- Refresh-token hashing + rotation
- MongoDB TTL cleanup
- BCrypt passwords
- Per-user file ownership
- React Axios 401 -> refresh -> retry flow
- Upload/search/download/delete

## Run
1. `docker compose up -d`
2. `cd backend && mvn spring-boot:run`
3. another terminal: `cd frontend && npm install && npm run dev`
4. open http://localhost:5173

Demo user:
`prem / Password@123`

Backend: http://localhost:8080
Health: http://localhost:8080/api/health

MongoDB stores users, refresh sessions and file metadata. Actual file bytes are stored in `./uploads`.

Production TODO: HTTPS, secret manager, strict CORS, CSRF strategy for cookie authentication, rate limiting, malware scanning, object storage and stronger operational key management.
