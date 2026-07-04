# Frontend Architecture

The current frontend is a static browser application built with HTML, CSS, and vanilla JavaScript.

## Current Flow

```text
HTML page
  |
  v
Feature script
  |
  v
src/shared/storage.js
  |
  v
localStorage
```

Phase 8 adds a migration/sync layer:

```text
localStorage
  |
  v
src/shared/local-data-migration.js
  |
  v
src/shared/api-client.js
  |
  v
Spring Boot APIs
```

## Feature Inventory

| Feature | Path | Current Persistence |
| --- | --- | --- |
| Dashboard | `index.html` | None |
| Routine | `src/features/routine/` | `localStorage` |
| Water | `src/features/water/` | `localStorage` |
| Workout | `src/features/workout/` | `localStorage` |
| Feedback | `src/features/feedback/` | `localStorage` |

## Local Storage Keys

| Key | Owner |
| --- | --- |
| `daily-tasks-{date}` | Routine completions |
| `customTasks` | Routine custom tasks |
| `holidayTasks` | Holiday routine tasks |
| `holidayTasksChecked` | Holiday routine completion state |
| `affirmations` | Affirmations |
| `history` | Water tracker |
| `pushupProgress` | Push-up challenge |
| `maintenanceRecords` | Maintenance tracker |
| `completedDays` | Maintenance tracker |
| `feedbackList` | Feedback form |
| `mindmirrorApiBaseUrl` | API client base URL override |
| `mindmirrorAccessToken` | JWT access token for protected API calls |
| `mindmirrorAuth` | Serialized auth response |
| `mindmirrorMigrationQueue` | Failed uploads waiting for retry |
| `mindmirrorMigratedOperations` | Successfully uploaded operation IDs |
| `mindmirrorMigrationStatus` | Last migration state for UI display |

## Readability Rules

- Keep page markup in HTML.
- Keep feature behavior in a feature JavaScript file when the script grows beyond simple initialization.
- Keep shared storage/date/API helpers under `src/shared`.
- Keep global layout and shared visual rules in `src/styles/global.css`.
- Keep feature-only CSS beside the feature page.

## Backend Integration Path

The shared API client is implemented in:

```text
src/shared/api-client.js
```

Responsibilities:

- Base URL handling.
- JSON request/response handling.
- Error normalization.
- Auth header attachment from `mindmirrorAccessToken`, `accessToken`, `mindmirrorAuth`, or `auth`.
- Session persistence through `MindMirrorApi.setSession(authResponse)`.

## Local Data Migration

Implemented in:

```text
src/shared/local-data-migration.js
```

The migration layer:

- Reads legacy localStorage keys.
- Converts them to backend request payloads.
- Uploads data after a token is available.
- Uses backend upsert endpoints where possible for conflict resolution.
- Stores failed uploads in `mindmirrorMigrationQueue`.
- Retries queued uploads manually, after login, and on browser `online`.
- Leaves localStorage feature data intact as the offline fallback.

Conflict behavior:

- Water, push-up challenge, push-up maintenance, and routine completions use `PUT` upserts.
- Routine and affirmation creates reactivate matching inactive rows on the backend.
- The client records successful operation IDs in `mindmirrorMigratedOperations` to avoid repeating completed uploads.

Migration approach for future pages:

1. Keep localStorage behavior working.
2. Add backend API for one feature.
3. Add sync or migration logic for that feature.
4. Repeat feature by feature.
