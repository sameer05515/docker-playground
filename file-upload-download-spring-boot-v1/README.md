# Spring Boot File Upload / Download V1

Simple Spring Boot MVC application for uploading, listing, downloading and deleting files.

## Stack
- Java 17
- Spring Boot 3.4.5
- Spring MVC
- Thymeleaf
- Maven
- Local filesystem

## Run

```bash
mvn spring-boot:run
```

Open:

http://localhost:8080

Or:

```bash
mvn clean package
java -jar target/file-upload-download-0.0.1-SNAPSHOT.jar
```

## Endpoints

- `GET /` - web UI
- `POST /upload` - upload multipart file
- `GET /download/{fileName}` - download
- `POST /delete/{fileName}` - delete

## Storage

Files are stored in `./uploads`.

Change it in `application.properties`:

```properties
file.storage.location=D:/data/my-uploads
```

Maximum upload size is 20 MB.

## Production improvements for V2

- REST API
- Database metadata
- UUID storage names
- MIME/extension validation
- User authentication and authorization
- Per-user files
- S3/object storage
- Virus scanning
- File quotas
- Pagination/search
