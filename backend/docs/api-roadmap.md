# API Roadmap

This document describes the backend API shape. Phase 3 authentication and user account endpoints are implemented; product domain APIs are still planned.

## Current Endpoints

```text
GET /api/health
```

Purpose:

- Confirm the application is running.
- Confirm the database is reachable.

### Auth

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

### Users

```text
GET    /api/users/me
PUT    /api/users/me
DELETE /api/users/me
```

Protected endpoints require a JWT access token:

```text
Authorization: Bearer <accessToken>
```

Auth responses include an access token, refresh token, token type, expiry seconds, and the authenticated user profile.

## Authentication Rules

- Passwords are stored with BCrypt.
- Access tokens are stateless JWTs signed with HMAC-SHA256.
- Refresh tokens are random opaque values; only SHA-256 hashes are stored.
- Refreshing rotates the refresh token by revoking the used token and issuing a new one.
- Logout revokes the submitted refresh token.
- Password change revokes all refresh tokens for the user.
- Deleted accounts are marked `DELETED`, renamed to a non-reusable placeholder email, and excluded from login.
- User role authorities use Spring Security `ROLE_<role>` format. `USER` is the default role.

## Validation Rules

- Email is required and must be valid.
- Duplicate emails return `409 Conflict`.
- Passwords must be 8-128 characters and include uppercase, lowercase, number, and symbol characters.
- `displayName` is required on registration and capped at 120 characters.

## Recommended API Versioning

Current Phase 3 endpoints use `/api` to match the requested contract. Future product APIs can use:

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
