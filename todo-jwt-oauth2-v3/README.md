# Todo JWT + OAuth2 + Keycloak V3

## Folder structure

```text
todo-jwt-oauth2-v3/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/example/todo/
├── frontend/
│   ├── package.json
│   └── src/
├── docker-compose.yml
└── README.md
```

## V3 logout

Local JWT and Keycloak-issued application JWTs can be revoked through `POST /api/auth/logout`.
Revoked JWTs are stored as SHA-256 hashes in MySQL and automatically cleaned after expiry.

Frontend logout removes the JWT from localStorage and calls the backend revoke endpoint.

For the current architecture, Keycloak SSO itself is separate from application-JWT revocation. To force a fresh Keycloak credential prompt as well, configure/use Keycloak end-session logout in the next iteration. The important V3 behavior is that the application JWT is invalid immediately after logout.

## Start

```powershell
docker compose up -d
cd backend
mvn spring-boot:run
```

Then:

```powershell
cd frontend
npm install
npm run dev
```

Keycloak realm: `todo-realm`
Client: `todo-client`
Redirect URI: `http://localhost:8081/login/oauth2/code/keycloak`
Web origin: `http://localhost:5173`
