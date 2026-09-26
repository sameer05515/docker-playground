# Todo JWT + OAuth2 + Keycloak V2

## Structure

- `backend/` - Spring Boot REST API, JWT and Keycloak OAuth2
- `frontend/` - React + Vite UI
- `docker-compose.yml` - MySQL + Keycloak

## V2 authentication architecture

Local JWT users are stored in MySQL.
Keycloak users are NOT inserted into MySQL.

Local: MySQL -> JWT provider=LOCAL
Keycloak: Keycloak -> OAuth2SuccessHandler -> JWT provider=KEYCLOAK

For KEYCLOAK JWTs the JWT filter creates the authenticated principal directly from the verified token and does not query the local users table.

## Run

```bash
docker compose up -d
cd backend
mvn spring-boot:run
```

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Backend: http://localhost:8081
Frontend: http://localhost:5173
Keycloak: http://localhost:8080

Keycloak realm: `todo-realm`
Client: `todo-client`
Redirect URI: `http://localhost:8081/login/oauth2/code/keycloak`
Web origin: `http://localhost:5173`
Client secret: `todo-secret`
