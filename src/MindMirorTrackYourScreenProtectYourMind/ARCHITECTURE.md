# MindMirror Architecture Report

## Project Overview

MindMirror is a static, browser-based digital wellness prototype. It runs directly from HTML files and stores user data in browser `localStorage`. There is no Java backend, MySQL database, API layer, authentication layer, package manager, build step, or test framework in the current codebase.

The application provides:

- Daily routine tracking
- Custom and holiday tasks
- Daily affirmations
- Push-up challenge and maintenance tracking
- Water intake tracking
- Local-only feedback capture

## Technology Stack

- HTML5
- CSS
- Plain JavaScript
- Browser `localStorage`
- SheetJS/XLSX from CDN for routine Excel export
- Static image asset under `src/assets/logo.png`

## Current Directory Structure

```text
.
|-- index.html
|-- README.md
|-- ARCHITECTURE.md
`-- src/
    |-- assets/
    |   `-- logo.png
    |-- styles/
    |   `-- global.css
    |-- shared/
    |   `-- storage.js
    `-- features/
        |-- routine/
        |   |-- routine.html
        |   |-- routine.css
        |   |-- routine.js
        |   `-- holiday-tasks.js
        |-- pushups/
        |   |-- challenge.html
        |   `-- maintenance.html
        |-- water/
        |   `-- water-tracker.html
        `-- feedback/
            `-- feedback.html
```

## Main Modules

### Dashboard

`index.html` is the entry point. It renders the product header, routine preview, push-up tracker iframe, water tracker iframe, feedback link, and footer.

`src/styles/global.css` contains dashboard layout, card styling, iframe sizing, and responsive behavior.

### Shared Utilities

`src/shared/storage.js` exposes `window.MindMirrorStorage`, a small wrapper around `localStorage` for safe JSON reads, JSON writes, deletion, and prefix-based key lookup.

### Routine

`src/features/routine/routine.html` defines the routine tracker screen and loads:

- `../../shared/storage.js`
- `routine.js`
- `holiday-tasks.js`
- SheetJS/XLSX CDN script

`routine.js` renders daily tasks, custom tasks, progress, previous-day history, Excel export, and affirmations.

`holiday-tasks.js` renders and persists holiday tasks.

### Pushups

`src/features/pushups/challenge.html` implements the 100-day push-up tracker and embeds `maintenance.html`.

`src/features/pushups/maintenance.html` implements the 365-day maintenance tracker.

Both pages use `src/shared/storage.js`.

### Water

`src/features/water/water-tracker.html` implements the 8-glasses-per-day tracker and date-wise history. Stored history rendering uses DOM APIs instead of injecting user-controlled HTML.

### Feedback

`src/features/feedback/feedback.html` captures local feedback in `localStorage`. No data is submitted to a server.

## Data Storage Map

| Key | Owner | Purpose |
| --- | --- | --- |
| `daily-tasks-{YYYY-MM-DD}` | routine | Daily checkbox state |
| `customTasks` | routine | User-created daily tasks |
| `lastOpenedDate` | routine | Daily reset state |
| `affirmations` | routine | Saved affirmations |
| `holidayTasks` | routine | Holiday task list |
| `holidayTasksChecked` | routine | Checked holiday task names |
| `pushupProgress` | pushups | 100-day push-up records |
| `completedDays` | pushups | 365-day maintenance count |
| `maintenanceRecords` | pushups | Push-up capacity records |
| `history` | water | Water intake history |
| `feedbackList` | feedback | Saved feedback entries |

Future work should migrate these to namespaced keys such as `mindmirror.water.history` and `mindmirror.feedback.entries`.

## Data Flow

```text
index.html
|-- src/features/routine/routine.html
|   |-- src/shared/storage.js
|   |-- routine.js
|   `-- holiday-tasks.js
|-- src/features/pushups/challenge.html
|   |-- src/shared/storage.js
|   `-- maintenance.html
|-- src/features/water/water-tracker.html
|   `-- src/shared/storage.js
`-- src/features/feedback/feedback.html
    `-- src/shared/storage.js
```

All persistence is local to the browser. The modules do not communicate through APIs; they share only browser storage.

## Security Notes

- User-controlled affirmations and water history are rendered through DOM APIs with `textContent`.
- Remaining dynamic HTML generation should be reviewed before adding rich user-generated content.
- The XLSX CDN dependency should be pinned or vendored before production use.
- Feedback is stored in browser storage and should not be treated as secure or durable.

## Performance Notes

- The dashboard loads multiple iframe pages at startup.
- The push-up tracker renders many rows at once.
- Routine history scans localStorage keys by prefix.
- XLSX loads on routine page load even when export is not used.

## Backend And Database Status

There is no backend or database in this repository. If MindMirror becomes a production product, add a backend only when requirements justify accounts, synchronization, reporting, or durable feedback collection.

Recommended Java backend structure:

```text
backend/
`-- src/main/java/.../
    |-- controller/
    |-- service/
    |-- repository/
    |-- dto/
    |-- entity/
    |-- config/
    |-- security/
    |-- exception/
    |-- validation/
    `-- util/
```

Recommended MySQL tables:

- `users`
- `routine_tasks`
- `routine_completions`
- `water_entries`
- `pushup_entries`
- `feedback_entries`

Recommended indexes:

- `(user_id, entry_date)`
- `(user_id, created_at)`
- unique `(user_id, task_date, task_id)` for daily routine completion rows

## Priority Roadmap

1. Namespace all storage keys while migrating old keys safely.
2. Extract remaining inline scripts and styles from feature HTML files.
3. Replace inline `onclick` handlers with `addEventListener`.
4. Lazy-load XLSX only when export is requested.
5. Add linting, formatting, and basic browser smoke tests.
6. Add import/export backup for local data.
7. Add backend, database, authentication, and authorization only when product requirements need durable multi-device data.
