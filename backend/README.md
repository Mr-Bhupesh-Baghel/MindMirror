# MindMirror Backend

Spring Boot REST API foundation for MindMirror.

The backend currently provides:

- Spring Boot application setup.
- MySQL datasource configuration.
- Flyway database migrations.
- Health check endpoint.
- Security baseline.

## Stack

| Technology | Version |
| --- | --- |
| Java | 17+ |
| Spring Boot | 3.3.5 |
| Maven | 3.9+ |
| MySQL | 8+ |
| Flyway | Managed by Spring Boot |

## Structure

```text
backend/
|-- docs/
|   |-- api-roadmap.md
|   `-- database-schema.md
|-- src/main/java/com/mindmirror/backend/
|   |-- config/
|   |-- health/
|   `-- MindMirrorBackendApplication.java
|-- src/main/resources/
|   |-- application.yml
|   `-- db/migration/
|-- src/test/
`-- pom.xml
```

Recommended domain package layout as APIs are added:

```text
com.mindmirror.backend.<domain>
|-- <Domain>Controller.java
|-- <Domain>Service.java
|-- <Domain>Repository.java
|-- dto/
`-- entity/
```

Examples of domains: `user`, `routine`, `water`, `workout`, `feedback`, `auth`.

## Configuration

Environment variables:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/mindmirror?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:SERVER_PORT="8081"
```

The default values are defined in `src/main/resources/application.yml`.

## Run

From `backend/`:

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Health check:

```text
GET http://localhost:8081/api/health
```

Expected shape:

```json
{
  "status": "UP",
  "database": true,
  "timestamp": "2026-06-29T00:00:00Z"
}
```

## Test

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" test
```

The current test starts the Spring context and validates basic configuration. Because Flyway is enabled, tests require a reachable MySQL database unless a dedicated test profile is added later.

## Database

Migration files:

```text
src/main/resources/db/migration/
```

Current migrations:

- `V1__initial_schema.sql`
- `V2__phase_2_core_schema.sql`
- `V3__seed_development_data.sql`

Rules:

- Never edit a migration after it has been applied to a shared database.
- Add a new `V<number>__description.sql` file for every schema change.
- Keep seed data safe for development and avoid real credentials or private user data.

See [Database Schema](docs/database-schema.md).

## API Direction

The backend should expose stable versioned APIs under:

```text
/api/v1
```

Before wiring the frontend to backend APIs, add:

- Request/response DTOs.
- Bean validation.
- Global exception handling.
- Domain services.
- Repository tests or integration tests for critical behavior.

See [API Roadmap](docs/api-roadmap.md).
