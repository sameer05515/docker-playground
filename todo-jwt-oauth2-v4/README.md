# Todo JWT + OAuth2 + Keycloak V4

## Roles

`ROLE_USER`
- See own todos
- Create own todos
- Update own todos
- Delete own todos

`ROLE_ADMIN`
- See all users' todos
- Cannot create todos
- Cannot update todos
- Cannot delete todos

Authorization is enforced by Spring Security on the backend. Frontend button hiding is only UX.

## Identity / ownership

Each Todo has an `ownerId`.

Local JWT:
`LOCAL:<username>`

Keycloak:
`KEYCLOAK:<keycloak-sub>`

The Keycloak `sub` is used instead of the username so local and Keycloak users with the same display name cannot accidentally share Todo ownership.

The backend never accepts `ownerId` from the request body.

## Local roles

New registrations always receive `USER`. To make an existing local user an admin:

```sql
UPDATE users SET role='ADMIN' WHERE username='admin';
```

The JWT generated at the next login will contain `ROLE_ADMIN`.

## Keycloak roles

In realm `todo-realm`, create realm roles exactly:

- `USER`
- `ADMIN`

Assign `USER` to normal users and `ADMIN` to admin users.

The application uses the OIDC ID token's `realm_access.roles` claim. The standard Keycloak `roles` client scope should be enabled for the client. If you customized client scopes/mappers, make sure realm roles are included in the ID token.

## Keycloak client

Client: `todo-client`

Client authentication: ON

Standard flow: ON

Valid redirect URI:

```text
http://localhost:8081/login/oauth2/code/keycloak
```

Web origin:

```text
http://localhost:5173
```

Valid post logout redirect URI:

```text
http://localhost:5173/*
```

The secret in `backend/src/main/resources/application.yml` must match Keycloak.

## Run

Start infrastructure:

```bash
docker compose up -d
```

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

## Existing V3 database

V4 adds `owner_id`. Existing Todo rows may have a null owner because V3 did not necessarily assign ownership. For a clean development test, either delete old test rows:

```sql
DELETE FROM todos;
```

or assign an appropriate `owner_id` manually before testing.

## Logout

The application revokes the JWT in `revoked_tokens`. For Keycloak tokens it also returns a Keycloak end-session URL, so the React client clears the application token and redirects through Keycloak logout.
