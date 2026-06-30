# MindMirror Backend

Spring Boot REST API foundation for MindMirror.

The backend currently provides:

- Spring Boot application setup.
- MySQL datasource configuration.
- Flyway database migrations.
- Health check endpoint.
- JWT authentication and protected user APIs.
- Feedback CRUD with database storage and pagination.
- Water tracking APIs with daily upsert, history, stats, and streaks.
- Spring Security with BCrypt password hashing, CORS, and role-based access control.

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
|   |-- auth/
|   |-- config/
|   |-- exception/
|   |-- feedback/
|   |-- health/
|   |-- security/
|   |-- user/
|   |-- validation/
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
$env:JWT_SECRET="replace-with-a-long-random-secret"
$env:JWT_ACCESS_TOKEN_TTL="15m"
$env:JWT_REFRESH_TOKEN_TTL="30d"
```

The default values are defined in `src/main/resources/application.yml`.

`JWT_SECRET` should be replaced outside development. Use at least 32 random bytes.

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

## Authentication

Implemented auth endpoints:

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout

GET    /api/users/me
PUT    /api/users/me
DELETE /api/users/me

POST   /api/feedback
GET    /api/feedback?page=0&size=20
GET    /api/feedback/{id}
DELETE /api/feedback/{id}

GET    /api/water?date=YYYY-MM-DD
PUT    /api/water
GET    /api/water/history
GET    /api/water/stats
```

`POST /api/auth/register` and `POST /api/auth/login` return:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "opaque-refresh-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "displayName": "User",
    "role": "USER",
    "createdAt": "2026-06-29T00:00:00Z",
    "updatedAt": "2026-06-29T00:00:00Z"
  }
}
```

Protected endpoints require:

```text
Authorization: Bearer <accessToken>
```

`POST /api/feedback` is public. If a valid JWT is included, the submitted feedback is linked to that user. Listing, reading, and deleting feedback use the default protected API rule.

Feedback create request:

```json
{
  "name": "User Name",
  "email": "user@example.com",
  "rating": 5,
  "message": "This helped me stay consistent.",
  "feedbackDate": "2026-06-29"
}
```

Registration validates email format, prevents duplicate email addresses, and requires passwords to be 8-128 characters with uppercase, lowercase, number, and symbol characters. Passwords are stored with BCrypt. Refresh tokens are random opaque tokens; only SHA-256 hashes are stored in MySQL.

`PUT /api/users/me` updates the profile and can change the password:

```json
{
  "displayName": "New Name",
  "currentPassword": "OldPassword1!",
  "newPassword": "NewPassword1!"
}
```

Water entries are protected by JWT and unique per authenticated user/date. `PUT /api/water` creates or updates a daily record:

```json
{
  "entryDate": "2026-06-30",
  "glasses": 8,
  "goalGlasses": 8
}
```

`GET /api/water/history` returns water history newest first and accepts optional `from` and `to` date filters.

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
- `V4__auth_refresh_tokens.sql`

Rules:

- Never edit a migration after it has been applied to a shared database.
- Add a new `V<number>__description.sql` file for every schema change.
- Keep seed data safe for development and avoid real credentials or private user data.

See [Database Schema](docs/database-schema.md).

## API Direction

Current backend APIs are exposed under `/api`. Future product APIs can be versioned under `/api/v1` when the frontend integration begins.

Before wiring the frontend to backend APIs, continue adding:

- Request/response DTOs.
- Bean validation.
- Global exception handling.
- Domain services.
- Repository tests or integration tests for critical behavior.

See [API Roadmap](docs/api-roadmap.md).
