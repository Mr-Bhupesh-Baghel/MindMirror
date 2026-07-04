# MindMirror Backend

Spring Boot REST API foundation for MindMirror.

The backend currently provides:

- Spring Boot application setup.
- MySQL datasource configuration.
- Flyway database migrations.
- Health check endpoint.
- JWT authentication and protected user APIs.
- Routine task, completion, history, and CSV export APIs.
- Feedback CRUD with database storage and pagination.
- Water tracking APIs with daily upsert, history, stats, and streaks.
- Push-up challenge and maintenance APIs with permanent progress, history, and streak stats.
- Affirmation APIs with permanent user-scoped storage.
- Migration and sync status APIs for account-based browser data migration.
- Admin dashboard APIs for user management, feedback review, analytics, and CSV/XLSX exports.
- Spring Security with BCrypt password hashing, CORS, and role-based access control.
- Production readiness controls: global API errors, rate limiting, secure headers, actuator monitoring, Docker, Compose, and CI.

## Stack

| Technology | Version |
| --- | --- |
| Java | 17+ |
| Spring Boot | 3.3.5 |
| Maven | 3.9+ |
| MySQL | 8+ |
| Flyway | Managed by Spring Boot |
| Actuator/Micrometer | Managed by Spring Boot |

## Structure

```text
backend/
|-- docs/
|   |-- api-roadmap.md
|   `-- database-schema.md
|-- src/main/java/com/mindmirror/backend/
|   |-- auth/
|   |-- admin/
|   |-- config/
|   |-- exception/
|   |-- feedback/
|   |-- health/
|   |-- pushups/
|   |-- routine/
|   |-- security/
|   |-- sync/
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
$env:SPRING_PROFILES_ACTIVE="dev"
$env:JWT_SECRET="replace-with-a-long-random-secret"
$env:JWT_ACCESS_TOKEN_TTL="15m"
$env:JWT_REFRESH_TOKEN_TTL="30d"
$env:APP_CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173,http://localhost:8080"
$env:APP_RATE_LIMIT_ENABLED="true"
$env:APP_RATE_LIMIT_REQUESTS_PER_MINUTE="120"
```

The default values are defined in `src/main/resources/application.yml`.

`JWT_SECRET` and `DB_PASSWORD` must be replaced outside development. The `prod` profile fails startup if development defaults are used.

## Run

From `backend/`:

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Health check:

```text
GET http://localhost:8081/api/health
GET http://localhost:8081/actuator/health
GET http://localhost:8081/actuator/health/readiness
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

POST /api/migration/start
GET  /api/migration/status
POST /api/sync
GET  /api/sync/status

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

GET    /api/pushups/challenge
PUT    /api/pushups/challenge
GET    /api/pushups/challenge/history
GET    /api/pushups/maintenance
PUT    /api/pushups/maintenance
GET    /api/pushups/maintenance/history

GET    /api/routine/tasks
POST   /api/routine/tasks
PATCH  /api/routine/tasks/{id}
DELETE /api/routine/tasks/{id}
GET    /api/routine/completions
PUT    /api/routine/completions
GET    /api/routine/history
GET    /api/routine/history/export

GET    /api/affirmations
POST   /api/affirmations
DELETE /api/affirmations/{id}

GET    /api/admin/users
PATCH  /api/admin/users/{id}
DELETE /api/admin/users/{id}
GET    /api/admin/feedback
GET    /api/admin/stats
GET    /api/admin/export
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

Migration and sync endpoints are protected. `POST /api/migration/start` marks the authenticated user's first-login migration as running. The browser then uploads localStorage-derived records through the normal domain APIs and reports the result with `POST /api/sync`:

```json
{
  "state": "complete",
  "uploaded": 12,
  "failed": 0,
  "queued": 0,
  "conflicts": 3,
  "lastError": null
}
```

`GET /api/migration/status` and `GET /api/sync/status` return the same status shape with migration state, sync state, queued/failed/uploaded/conflict counts, last error, and timestamps.

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

Push-up challenge entries are protected by JWT and unique per authenticated user/challenge day. `PUT /api/pushups/challenge` creates or updates challenge progress:

```json
{
  "entryDate": "2026-07-04",
  "challengeDay": 42,
  "targetCount": 42,
  "completedCount": 42
}
```

Push-up maintenance entries are protected by JWT and unique per authenticated user/date. `PUT /api/pushups/maintenance` creates or updates daily maintenance:

```json
{
  "entryDate": "2026-07-04",
  "pushupsCount": 50,
  "challengeDay": 42
}
```

Challenge and maintenance history endpoints return newest-first records. Current progress responses include statistics such as total days, total push-ups, average per day, completion rate, current streak, and longest streak.

Routine tasks are protected by JWT and scoped to the authenticated user. Default daily and holiday tasks are created for a user when routine endpoints are first read. `POST /api/routine/tasks` creates custom tasks by default:

```json
{
  "title": "Read one page",
  "category": "custom",
  "sortOrder": 1
}
```

`PUT /api/routine/completions` upserts daily completion state for one or more tasks:

```json
{
  "completionDate": "2026-07-04",
  "completions": [
    {
      "taskId": 1,
      "completed": true
    }
  ]
}
```

`GET /api/routine/history` returns newest-first daily summaries with completion percentages, and `GET /api/routine/history/export` returns the same summary as CSV for export.

Affirmations are protected by JWT and stored per user:

```json
{
  "text": "Small daily actions build discipline."
}
```

Admin endpoints are protected by `ROLE_ADMIN`. They provide paginated user and feedback review, soft-delete account management, aggregate analytics, and file exports:

```text
GET /api/admin/stats
GET /api/admin/export?dataset=users&format=csv
GET /api/admin/export?dataset=feedback&format=xlsx
GET /api/admin/export?dataset=stats&format=csv
```

`GET /api/admin/stats` includes daily active users, water streaks, push-up streaks, routine completion rate, and seven-day retention.

## Test

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" test
```

Tests run with the `test` profile, H2 in-memory storage, and rate limiting disabled. Current coverage includes context startup, health API, validation error shape, security headers, rate limiting, and domain service behavior.

## Production Readiness

Phase 10 adds consistent global exception handling, secure headers, production CORS, API rate limiting, actuator monitoring, Docker, Compose, GitHub Actions CI, production secret validation, and database backup guidance.

See [Production Readiness](../docs/production-readiness.md).

## Docker

From the repository root:

```powershell
$env:JWT_SECRET="replace-with-at-least-32-random-characters"
$env:DB_PASSWORD="replace-with-db-password"
$env:MYSQL_ROOT_PASSWORD="replace-with-root-password"
docker compose up --build
```

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
- `V5__sync_status.sql`

Phase 9 admin and analytics uses the existing tables and does not require a new migration.

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
