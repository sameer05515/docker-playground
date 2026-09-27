# Node.js + EJS Todo + Keycloak + MySQL

A server-side Todo application using:

- Node.js
- Express
- EJS
- Passport
- OpenID Connect
- Keycloak
- MySQL
- Docker Compose

## 1. Start Keycloak and MySQL

From the project root:

```bash
docker compose up -d
```

Check:

```bash
docker compose ps
```

Keycloak:

http://localhost:8080

Keycloak admin:

- Username: `admin`
- Password: `admin`

The realm `todo-realm` and client `todo-node-app` are imported automatically.

## 2. Start Node.js application

Open a second terminal:

```bash
cd backend
npm install
```

Create `.env`:

Windows CMD:

```cmd
copy .env.example .env
```

PowerShell:

```powershell
Copy-Item .env.example .env
```

Linux/macOS:

```bash
cp .env.example .env
```

Then:

```bash
npm start
```

Application:

http://localhost:3000

## 3. Login

Click:

```text
Login with Keycloak
```

Demo user:

```text
username: prem
password: password
```

After successful authentication Keycloak redirects to:

```text
http://localhost:3000/auth/callback
```

Then the user is redirected to:

```text
http://localhost:3000/todos
```

## 4. Database

MySQL:

```text
host: localhost
port: 3306
database: todo_db
username: todo_user
password: todo_pass
```

The Node.js application automatically creates:

```text
todos
sessions
```

tables.

## 5. Authentication flow

```text
Browser
   |
   | GET /auth/login
   v
Node.js
   |
   | Authorization Code
   v
Keycloak
   |
   | login
   v
Browser
   |
   | /auth/callback?code=...
   v
Node.js
   |
   | token exchange
   v
Keycloak
   |
   | ID token / user info
   v
Node.js
   |
   | session cookie
   v
Browser
```

The application does NOT store the Keycloak access token in localStorage.

The server maintains the authenticated user in an Express session.

## 6. Stop everything

```bash
docker compose down
```

To remove MySQL data as well:

```bash
docker compose down -v
```

WARNING: `-v` deletes the MySQL Docker volume.

## 7. Troubleshooting

### Port 8080 already in use

Change:

```yaml
ports:
  - "8081:8080"
```

Then also update the Keycloak URLs in:

```text
backend/.env
```

For example:

```env
KEYCLOAK_ISSUER=http://localhost:8081/realms/todo-realm
KEYCLOAK_AUTHORIZATION_URL=http://localhost:8081/realms/todo-realm/protocol/openid-connect/auth
KEYCLOAK_TOKEN_URL=http://localhost:8081/realms/todo-realm/protocol/openid-connect/token
KEYCLOAK_USERINFO_URL=http://localhost:8081/realms/todo-realm/protocol/openid-connect/userinfo
KEYCLOAK_LOGOUT_URL=http://localhost:8081/realms/todo-realm/protocol/openid-connect/logout
```

### Port 3306 already in use

Change MySQL mapping:

```yaml
ports:
  - "3307:3306"
```

Then:

```env
MYSQL_PORT=3307
```

## Important

This project is intended as a learning/demo project.

For production, add:

- HTTPS
- secure cookies
- CSRF protection
- stronger session secret
- reverse proxy
- production Keycloak configuration
- database migrations
- input validation
- authorization/roles
- refresh-token/session strategy
- centralized error handling
