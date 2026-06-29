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

## Readability Rules

- Keep page markup in HTML.
- Keep feature behavior in a feature JavaScript file when the script grows beyond simple initialization.
- Keep shared storage/date/API helpers under `src/shared`.
- Keep global layout and shared visual rules in `src/styles/global.css`.
- Keep feature-only CSS beside the feature page.

## Backend Integration Path

Add a shared API client before wiring pages directly to `fetch`.

Recommended future file:

```text
src/shared/api-client.js
```

Recommended responsibilities:

- Base URL handling.
- JSON request/response handling.
- Error normalization.
- Auth header attachment after authentication exists.

Migration approach:

1. Keep localStorage behavior working.
2. Add backend API for one feature.
3. Add sync or migration logic for that feature.
4. Repeat feature by feature.
