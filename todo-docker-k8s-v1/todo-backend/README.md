# Todo Backend

Spring Boot REST API for the Todo application.

## Run without Docker

Requirements:
- Java 17+
- Maven 3.9+

```bash
mvn spring-boot:run
```

API:
- GET    /api/todos
- POST   /api/todos
- PUT    /api/todos/{id}
- DELETE /api/todos/{id}

H2 console:
http://localhost:8080/h2-console

JDBC URL:
jdbc:h2:file:./data/tododb
