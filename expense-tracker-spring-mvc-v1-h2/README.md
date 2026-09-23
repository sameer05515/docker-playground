# Expense Tracker - V1 (H2)

Spring Boot MVC + Thymeleaf + Spring Data JPA + H2.

This version intentionally does **not require MySQL**.

The goal is to keep the application database-independent while learning Docker separately. In a later version, MySQL can be introduced as a Docker container.

## Requirements

- Java 17+
- Maven

No MySQL installation is required.

## Run

```bash
mvn clean spring-boot:run
```

Open:

```text
http://localhost:8080
```

## H2 Console

Open:

```text
http://localhost:8080/h2-console
```

Use:

```text
JDBC URL: jdbc:h2:file:./data/expense_tracker
User:     sa
Password: [leave blank]
```

## Features

- Dashboard
- Add expense
- Edit expense
- Delete expense
- List expenses
- Categories
- Monthly total
- Overall total
- Validation
- Persistent H2 database
- H2 web console

## URLs

```text
GET  /
GET  /expenses
GET  /expenses/new
POST /expenses
GET  /expenses/{id}/edit
POST /expenses/{id}/delete
GET  /h2-console
```

## Database

The database is stored locally under:

```text
./data/expense_tracker.mv.db
```

Hibernate creates/updates the schema automatically:

```properties
spring.jpa.hibernate.ddl-auto=update
```

## Next Docker-focused evolution

```text
V1
Spring MVC + Thymeleaf + H2
        ↓
V2
Search + pagination + sorting + filters
        ↓
V3
MySQL running in Docker
        ↓
V4
Spring Boot + MySQL Docker Compose
        ↓
V5
React + Vite frontend
        ↓
V6
Docker images for frontend/backend
        ↓
V7
Kubernetes
```
