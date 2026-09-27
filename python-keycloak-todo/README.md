# Python Todo MVC + Keycloak + MySQL

A small MVC-style Todo application built with Python Flask, Jinja2 templates, SQLAlchemy and Keycloak OIDC authentication.

## Architecture

Browser -> Flask MVC (localhost:5000) -> MySQL (Docker)
                       |
                       +-> Keycloak OIDC (Docker, localhost:8080)

Only Keycloak and MySQL run in Docker. Flask runs locally so you can debug it easily from PyCharm/VS Code.

## Requirements

- Python 3.11+
- Docker Desktop
- Docker Compose

## 1. Start Keycloak and MySQL

```powershell
docker compose up -d

docker compose ps
```

Keycloak: http://localhost:8080
Admin console: http://localhost:8080/admin
Admin user: `admin`
Admin password: `admin`

The realm `todo-realm`, client `todo-app`, and demo user are imported automatically on first startup.

Demo application user:

- Username: `demo`
- Password: `demo123`

## 2. Create Python environment

Windows PowerShell:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## 3. Configure environment

```powershell
Copy-Item .env.example .env
```

The supplied `.env.example` already points to MySQL Docker port `3307`.

## 4. Run Flask

```powershell
python run.py
```

Open http://localhost:5000

Click **Login with Keycloak**.

## 5. MVC structure

```text
app/
  controllers/
    auth_controller.py
    todo_controller.py
  models/
    todo.py
  templates/
    base.html
    home.html
    todos/list.html
  static/
    style.css
  __init__.py
run.py
```

### Controller
`todo_controller.py` contains HTTP routes and application flow.

### Model
`todo.py` contains the SQLAlchemy Todo entity.

### View
Jinja2 templates under `app/templates` render HTML.

## Authentication flow

1. User opens `/todos`.
2. Flask checks `session['user']`.
3. If absent, Flask redirects to `/login`.
4. Authlib redirects the browser to Keycloak.
5. Keycloak authenticates the user.
6. Keycloak redirects to `/authorize` with an authorization code.
7. Authlib exchanges the code for OIDC tokens.
8. Flask stores the authenticated user in the server-side session cookie.
9. Todo operations are filtered by the authenticated username.

## Important

This project is intentionally a learning/development setup. Change the client secret, Flask secret, admin password and demo password before using it outside local development.

The Keycloak image is pinned to 26.7.4, the current Keycloak release at the time this project was generated.
