# Todo Spring Boot MVC - GitHub OAuth2

## Features

- Spring Boot MVC + Thymeleaf
- GitHub OAuth2 Login
- MySQL 8 Docker
- `ROLE_USER` and `ROLE_ADMIN`
- First logged-in user can be configured as admin
- USER can CRUD only their own todos
- ADMIN can see all todos
- Server-side ownership checks
- Logout through Spring Security

## Requirements

- Java 17+
- Maven 3.9+
- Docker Desktop
- GitHub account

## 1. Start MySQL

```bash
docker compose up -d
```

## 2. Create GitHub OAuth App

Open GitHub:

Settings -> Developer settings -> OAuth Apps -> New OAuth App

Use:

Application name:
Todo Spring Boot MVC

Homepage URL:
http://localhost:8080

Authorization callback URL:
http://localhost:8080/login/oauth2/code/github

Set environment variables.

Windows PowerShell:

```powershell
$env:GITHUB_CLIENT_ID="your-client-id"
$env:GITHUB_CLIENT_SECRET="your-client-secret"
```

Or create `.env` based on `.env.example` and export the variables before starting the application.

## 3. Run

```bash
mvn spring-boot:run
```

Open:

http://localhost:8080

Click `Login with GitHub`.

## Admin setup

By default, a newly created user gets `ROLE_USER`.

To make a user admin, run:

```sql
UPDATE users SET role = 'ROLE_ADMIN' WHERE github_id = 'YOUR_GITHUB_ID';
```

You can inspect users:

```sql
docker exec -it todo-mysql mysql -utodo_user -ptodo_password todo_db
```

Then:

```sql
SELECT id, github_id, username, name, email, role FROM users;
```

## Security model

USER:

- `/todos` -> own todos
- create/update/delete -> own todos only

ADMIN:

- `/admin/todos` -> all todos
- `/todos` -> can also access the normal todo area

The service layer also validates ownership, so changing a URL such as `/todos/10/edit` cannot be used to edit another user's todo.

## Callback

```text
http://localhost:8080/login/oauth2/code/github
```

## Build troubleshooting

This version intentionally does not use Lombok. If you previously had Lombok/JDK
compiler issues, run:

```bash
mvn clean package
```

Then:

```bash
mvn spring-boot:run
```
