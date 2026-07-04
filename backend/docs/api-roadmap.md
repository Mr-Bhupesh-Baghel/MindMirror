# API Roadmap

This document describes the backend API shape. Phase 7 routine tracking endpoints are implemented; the remaining product domain APIs are still planned.

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

### Feedback

```text
POST   /api/feedback
GET    /api/feedback?page=0&size=20
GET    /api/feedback/{id}
DELETE /api/feedback/{id}
```

`POST /api/feedback` accepts public feedback submissions and stores them in MySQL. If a valid JWT is supplied, the feedback is linked to that user; otherwise `userId` is stored as `null`.

Create request:

```json
{
  "name": "User Name",
  "email": "user@example.com",
  "rating": 5,
  "message": "This helped me stay consistent.",
  "feedbackDate": "2026-06-29"
}
```

`feedbackDate` is optional and defaults to the current server date.

Paginated list response:

```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "name": "User Name",
      "email": "user@example.com",
      "rating": 5,
      "message": "This helped me stay consistent.",
      "feedbackDate": "2026-06-29",
      "createdAt": "2026-06-29T00:00:00Z",
      "updatedAt": "2026-06-29T00:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Water

```text
GET  /api/water?date=YYYY-MM-DD
PUT  /api/water
GET  /api/water/history
GET  /api/water/history?from=YYYY-MM-DD&to=YYYY-MM-DD
GET  /api/water/stats
```

Water endpoints are protected and scoped to the authenticated user. Daily water entries are upserted by `(user_id, entry_date)`, preventing duplicate records while allowing device sync to update the same day.

Save request:

```json
{
  "entryDate": "2026-06-30",
  "glasses": 8,
  "goalGlasses": 8
}
```

`goalGlasses` is optional and defaults to `8` for new entries.

Daily response:

```json
{
  "entryDate": "2026-06-30",
  "glasses": 8,
  "goalGlasses": 8,
  "goalMet": true
}
```

Stats response:

```json
{
  "totalDays": 10,
  "goalDays": 7,
  "totalGlasses": 76,
  "currentStreak": 3,
  "longestStreak": 5,
  "averagePerDay": 7.6
}
```

### Routine

```text
GET    /api/routine/tasks
POST   /api/routine/tasks
PATCH  /api/routine/tasks/{id}
DELETE /api/routine/tasks/{id}

GET    /api/routine/completions
GET    /api/routine/completions?date=YYYY-MM-DD
PUT    /api/routine/completions
GET    /api/routine/history
GET    /api/routine/history?from=YYYY-MM-DD&to=YYYY-MM-DD
GET    /api/routine/history/export
GET    /api/routine/history/export?from=YYYY-MM-DD&to=YYYY-MM-DD
```

Routine endpoints are protected and scoped to the authenticated user. Default daily and holiday tasks are created lazily for a user when routine task or completion endpoints are first read. Custom tasks use the `custom` category by default.

Create task request:

```json
{
  "title": "Read one page",
  "category": "custom",
  "sortOrder": 1
}
```

Patch task request:

```json
{
  "title": "Read two pages",
  "category": "daily",
  "sortOrder": 2,
  "active": true
}
```

Task response:

```json
{
  "id": 1,
  "title": "Read one page",
  "category": "daily",
  "sortOrder": 1,
  "active": true
}
```

`DELETE /api/routine/tasks/{id}` deactivates the task so historical completion records remain available.

Completion save request:

```json
{
  "completionDate": "2026-07-04",
  "completions": [
    {
      "taskId": 1,
      "completed": true
    },
    {
      "taskId": 2,
      "completed": false
    }
  ]
}
```

Completion response:

```json
{
  "completionDate": "2026-07-04",
  "totalTasks": 2,
  "completedTasks": 1,
  "completionRate": 50.0,
  "tasks": [
    {
      "taskId": 1,
      "title": "Read one page",
      "category": "daily",
      "sortOrder": 1,
      "completed": true
    }
  ]
}
```

History response:

```json
[
  {
    "completionDate": "2026-07-04",
    "totalTasks": 2,
    "completedTasks": 1,
    "completionRate": 50.0
  }
]
```

`GET /api/routine/history/export` returns the same history summary as CSV with a `routine-history.csv` attachment filename.

### Push-Ups

```text
GET /api/pushups/challenge
PUT /api/pushups/challenge
GET /api/pushups/challenge/history

GET /api/pushups/maintenance
PUT /api/pushups/maintenance
GET /api/pushups/maintenance/history
```

Push-up endpoints are protected and scoped to the authenticated user.

Challenge entries are upserted by `(user_id, challenge_day)`, so a challenge day can be corrected without creating duplicates. If `targetCount` is omitted, it defaults to the submitted `challengeDay`.

Challenge save request:

```json
{
  "entryDate": "2026-07-04",
  "challengeDay": 42,
  "targetCount": 42,
  "completedCount": 42
}
```

### Affirmations

```text
GET    /api/affirmations
POST   /api/affirmations
DELETE /api/affirmations/{id}
```

Affirmation endpoints are protected and scoped to the authenticated user. Deletes deactivate affirmations so re-adding the same text can restore the existing row.

Create request:

```json
{
  "text": "Small daily actions build discipline."
}
```

Response:

```json
{
  "id": 1,
  "text": "Small daily actions build discipline."
}
```

Challenge progress response:

```json
{
  "entryDate": "2026-07-04",
  "challengeDay": 42,
  "targetCount": 42,
  "completedCount": 42,
  "status": "DONE",
  "challengeComplete": false,
  "totalDays": 42,
  "completedDays": 40,
  "totalTargetPushups": 903,
  "totalCompletedPushups": 891,
  "completionRate": 95.24,
  "currentStreak": 12,
  "longestStreak": 20
}
```

Maintenance entries are upserted by `(user_id, entry_date)` and track daily maintenance volume after or alongside the challenge.

Maintenance save request:

```json
{
  "entryDate": "2026-07-04",
  "pushupsCount": 50,
  "challengeDay": 42
}
```

Maintenance progress response:

```json
{
  "entryDate": "2026-07-04",
  "pushupsCount": 50,
  "challengeDay": 42,
  "totalDays": 30,
  "totalPushups": 1500,
  "averagePerDay": 50.0,
  "currentStreak": 8,
  "longestStreak": 14
}
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
- Feedback `name`, `email`, `rating`, and `message` are required.
- Feedback `rating` must be from 1 to 5.
- Feedback `name` is capped at 120 characters, `email` at 255 characters, and `message` at 5000 characters.
- Feedback pagination accepts `page >= 0` and `size` from 1 to 100.
- Water `entryDate` and `glasses` are required.
- Water `glasses` must be `>= 0`.
- Water `goalGlasses`, when supplied, must be `>= 1`.
- Routine task `title` is required and capped at 255 characters.
- Routine task `category` is optional, defaults to `custom`, and is capped at 80 characters.
- Routine completion `completionDate` and at least one completion item are required.
- Routine completion items require `taskId` and `completed`.
- Affirmation `text` is required and capped at 500 characters.
- Push-up challenge `entryDate`, `challengeDay`, and `completedCount` are required.
- Push-up challenge `challengeDay` must be `>= 1`, `completedCount` must be `>= 0`, and optional `targetCount` must be `>= 1`.
- Push-up maintenance `entryDate` and `pushupsCount` are required.
- Push-up maintenance `pushupsCount` and optional `challengeDay` must be `>= 1`.

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
Implemented under `/api/routine` for Phase 7.
```

### Water

```text
Implemented under `/api/water` for Phase 5.
```

### Workout

```text
Implemented under `/api/pushups` for Phase 6.
```

### Feedback

```text
Implemented under `/api/feedback` for Phase 4.
```

### Affirmations

```text
Implemented under `/api/affirmations` for Phase 7.
```

## Implementation Rules

- Controllers should accept and return DTOs, not entities.
- Services should contain business rules.
- Repositories should only handle persistence.
- Use validation annotations on request DTOs.
- Add indexes for common query filters before APIs depend on them.
- Keep date values as ISO `yyyy-mm-dd`.
