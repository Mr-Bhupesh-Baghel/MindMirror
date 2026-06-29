# API Roadmap

This document describes the planned backend API shape. The current implemented endpoint is the health check.

## Current Endpoint

```text
GET /api/health
```

Purpose:

- Confirm the application is running.
- Confirm the database is reachable.

## Recommended API Versioning

New product APIs should use:

```text
/api/v1
```

Example:

```text
GET /api/v1/routine/tasks
POST /api/v1/routine/tasks
```

## Planned Domains

| Domain | Responsibility |
| --- | --- |
| Auth | Login, registration, token/session lifecycle |
| Users | Profile and account ownership |
| Routine | Routine tasks and daily completions |
| Water | Daily water entries |
| Workout | Push-up challenge and maintenance entries |
| Feedback | Feedback submissions |
| Affirmations | User affirmations |

## Endpoint Sketch

### Auth

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/logout
GET  /api/v1/auth/me
```

### Routine

```text
GET    /api/v1/routine/tasks
POST   /api/v1/routine/tasks
PUT    /api/v1/routine/tasks/{id}
DELETE /api/v1/routine/tasks/{id}

GET    /api/v1/routine/completions?date=yyyy-mm-dd
PUT    /api/v1/routine/completions/{taskId}
```

### Water

```text
GET /api/v1/water/entries?from=yyyy-mm-dd&to=yyyy-mm-dd
PUT /api/v1/water/entries/{date}
```

### Workout

```text
GET  /api/v1/workout/pushups
POST /api/v1/workout/pushups

GET  /api/v1/workout/maintenance
POST /api/v1/workout/maintenance
```

### Feedback

```text
POST /api/v1/feedback
GET  /api/v1/feedback
```

### Affirmations

```text
GET    /api/v1/affirmations
POST   /api/v1/affirmations
PUT    /api/v1/affirmations/{id}
DELETE /api/v1/affirmations/{id}
```

## Implementation Rules

- Controllers should accept and return DTOs, not entities.
- Services should contain business rules.
- Repositories should only handle persistence.
- Use validation annotations on request DTOs.
- Add indexes for common query filters before APIs depend on them.
- Keep date values as ISO `yyyy-mm-dd`.
