FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw -q -DskipTests package 2>/dev/null ||     (apt-get update && apt-get install -y maven && mvn -q -DskipTests package)

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/todo-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
