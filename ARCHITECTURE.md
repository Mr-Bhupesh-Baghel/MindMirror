# MindMirror Architecture

## Purpose

MindMirror helps users track habits and wellness routines while the project evolves from a local browser prototype into a backend-backed application.

Core product areas:

- Routine tracking.
- Water tracking.
- Push-up challenge tracking.
- Push-up maintenance tracking.
- Affirmations.
- Feedback.

## Current Architecture

```text
Browser UI
  |
  | localStorage today
  v
Browser persistence

Spring Boot API
  |
  | Flyway + JDBC/JPA foundation
  v
MySQL
```

The frontend currently reads and writes directly to `localStorage`. The backend and database foundation are now present, but the frontend is not yet wired to backend APIs.

## Technology Stack

| Layer | Technology |
| --- | --- |
| Frontend | HTML, CSS, vanilla JavaScript |
| Browser storage | `localStorage` |
| Backend | Spring Boot 3, Spring Web, Spring Security, Spring Data JPA |
| Database | MySQL 8+ |
| Migrations | Flyway |
| Build | Maven |

## Frontend Modules

```text
src/features/
|-- feedback/
|-- routine/
|-- water/
`-- workout/
```

Guidelines:

- Put feature-specific HTML, CSS, and JavaScript in the feature folder.
- Put reusable utilities in `src/shared`.
- Put global styling in `src/styles/global.css`.
- Avoid adding new inline scripts when a feature script file is reasonable.

## Backend Modules

Current packages:

```text
com.mindmirror.backend
|-- config
`-- health
```

Recommended package growth:

```text
com.mindmirror.backend
|-- auth
|-- common
|-- config
|-- feedback
|-- health
|-- routine
|-- user
|-- water
`-- workout
```

Each domain package should own its controller, service, repository, DTOs, and entity classes unless a shared abstraction is clearly useful.

## Database Foundation

Database migrations are stored in:

```text
backend/src/main/resources/db/migration/
```

Phase 2 tables:

- `users`
- `routine_tasks`
- `routine_completions`
- `water_entries`
- `pushup_entries`
- `maintenance_entries`
- `feedback_entries`
- `affirmations`

See [Database Schema](backend/docs/database-schema.md) for the ER diagram, relationships, indexes, and constraints.

## Data Flow Today

```text
User
  |
  v
HTML page
  |
  v
Feature JavaScript
  |
  v
src/shared/storage.js
  |
  v
localStorage
```

## Target Data Flow

```text
User
  |
  v
Frontend feature page
  |
  v
API client
  |
  v
Spring Boot controller
  |
  v
Service layer
  |
  v
Repository layer
  |
  v
MySQL
```

## Scalability Direction

Short term:

- Keep current localStorage behavior stable.
- Add backend APIs domain by domain.
- Add request and response DTOs before exposing database entities.
- Add validation and error handling before large frontend integration.

Medium term:

- Add authentication and user ownership.
- Add localStorage-to-API migration flow.
- Add API versioning under `/api/v1`.
- Add integration tests for migrations and core endpoints.

Long term:

- Add analytics endpoints.
- Add admin/reporting views.
- Add observability, rate limiting, and deployment automation.
- Prepare desktop and AI integrations as separate modules.

## Quality Risks

| Risk | Current State | Direction |
| --- | --- | --- |
| Inline JavaScript | Several feature pages still contain inline scripts | Extract by feature as files grow |
| Duplicate date formats | Browser pages use mixed locale and ISO dates | Standardize on ISO `yyyy-mm-dd` for APIs |
| Backend auth | Basic security shell only | Add JWT/session strategy |
| API contracts | Not implemented yet | Define DTOs and docs before wiring UI |
| Tests | Minimal backend context test | Add service, API, and migration tests |

## Related Docs

- [Project Structure](docs/project-structure.md)
- [Frontend Architecture](docs/frontend-architecture.md)
- [Scalability Roadmap](docs/scalability-roadmap.md)
- [Backend README](backend/README.md)
- [API Roadmap](backend/docs/api-roadmap.md)
