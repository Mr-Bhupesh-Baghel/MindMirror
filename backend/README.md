# MindMirror Backend

Spring Boot API for MindMirror.

## Requirements

- Java 17+
- Maven 3.9+
- MySQL 8+

## Configuration

The backend reads database settings from environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/mindmirror?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=hLQJV74kHDM9ZBc8rCPz
SERVER_PORT=8081
```

## Run

```powershell
mvn spring-boot:run
```

Then check:

```text
GET http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "UP",
  "database": true,
  "timestamp": "2026-06-18T00:00:00Z"
}
```
