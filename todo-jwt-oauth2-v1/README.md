# Todo JWT + OAuth2 + Keycloak

## Stack
- Spring Boot 3.5
- Spring Security
- JWT
- OAuth2 Client
- Keycloak
- MySQL 8
- React + Vite

## Start infrastructure

```bash
docker compose up -d
```

Keycloak:
http://localhost:8080

Create:
- Realm: `todo-realm`
- Client: `todo-client`
- Client type: OpenID Connect
- Client authentication: ON
- Client secret: `todo-secret`
- Valid redirect URI: `http://localhost:8081/login/oauth2/code/keycloak`
- Web origin: `http://localhost:5173`

Add a test user in Keycloak and set email.

## Start backend

```bash
cd backend
mvn spring-boot:run
```

Backend: http://localhost:8081

## Start frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173

## JWT registration

```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "prem",
  "email": "prem@example.com",
  "password": "password"
}
```

Then login:

```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "prem",
  "password": "password"
}
```

Use returned accessToken:

```http
Authorization: Bearer <accessToken>
```

## OAuth2

Frontend button redirects to:

`/oauth2/authorization/keycloak`

After successful Keycloak login, Spring Boot creates/resolves the local user and issues an application JWT.

> This V1 intentionally demonstrates the complete JWT + OAuth2 flow. For production, replace the URL-token handoff with a one-time authorization-code exchange and add refresh-token rotation/revocation.
