# Todo JWT + OAuth2 + Keycloak — V3 Fixed

## Folder structure

- `backend/` - Spring Boot application
- `frontend/` - React + Vite application
- `docker-compose.yml` - MySQL + Keycloak

## V3 fixes

### 1. Todo ownership

Todos are now owned by the authenticated user.

The backend stores `owner_id` on each Todo and uses the authenticated JWT subject:

- Local JWT: local username
- Keycloak JWT: Keycloak preferred username

APIs use `findAllByOwnerId(...)` and `findByIdAndOwnerId(...)`, so one user cannot read/update/delete another user's Todo.

Existing Todo records created by the older V3 have `owner_id = NULL`; they will not appear in user Todo lists. This is intentional to avoid assigning old records to the wrong user.

### 2. Application JWT revocation

Logout revokes the current application JWT in `revoked_tokens`.

The JWT filter checks the revocation store on every protected request.

Expired revocation entries are cleaned every hour using an explicit modifying query.

### 3. Keycloak logout

For a Keycloak token, `/api/auth/logout` returns a Keycloak end-session URL.

React redirects the browser to that URL. Keycloak then clears its SSO session and redirects back to:

`http://localhost:5173/login`

### Keycloak client settings

Realm: `todo-realm`

Client: `todo-client`

Client authentication: ON

Standard flow: ON

Valid redirect URI:
`http://localhost:8081/login/oauth2/code/keycloak`

Web origin:
`http://localhost:5173`

Valid post logout redirect URI:
`http://localhost:5173/login`

Client secret must match `todo-secret`.

## Run

```powershell
docker compose up -d
cd backend
mvn spring-boot:run
```

In another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173
Backend: http://localhost:8081
Keycloak: http://localhost:8080

## Authentication model

### Local JWT

MySQL user -> login -> application JWT (`provider=LOCAL`) -> Todo APIs

### Keycloak

Keycloak -> OAuth2 -> application JWT (`provider=KEYCLOAK`) -> Todo APIs

Keycloak users are NOT inserted into MySQL.
