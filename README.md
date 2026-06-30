# MindMirror

MindMirror is a personal productivity and wellness application for tracking routines, water intake, push-up progress, affirmations, and feedback.

The project is currently a hybrid application:

- Frontend: static HTML, CSS, and vanilla JavaScript.
- Local storage: browser `localStorage` for the current UI.
- Backend: Spring Boot REST API foundation.
- Database: MySQL schema managed with Flyway migrations.

## Current Status

| Area | Status |
| --- | --- |
| Frontend habit screens | Working localStorage prototype |
| Backend foundation | Spring Boot app with health and auth endpoints |
| Database foundation | Flyway migrations for core tables and refresh tokens |
| Authentication | JWT, refresh tokens, BCrypt, and protected profile APIs |
| Water API | Daily entries, history, statistics, and streaks |
| API integration from frontend | Planned |

## Mobile Responsiveness Update

### Problems Found

| Problem | Why it happened | Fix applied |
| --- | --- | --- |
| Cards, forms, and buttons could overflow at 320px | Several pages used fixed pixel widths without global `box-sizing` | Added border-box sizing, `max-width: 100%`, fluid container widths, and mobile padding |
| Embedded feature pages clipped inside iframes | Iframe children had desktop-sized headings, fixed-width panels, or no mobile wrapping rules | Added iframe/media safeguards, smaller mobile headings, and wrapping constraints |
| Routine task rows became too wide | Task text, checkbox, and action buttons stayed on one row | Stacked task rows under 480px and used grid buttons under 768px |
| Water, feedback, and maintenance panels used fixed widths | `width: 320px` and `width: 350px` plus padding exceeded small phone width | Replaced with `width: min(100%, ...)` and border-box sizing |
| Touch targets were small or inconsistent | Some inputs and buttons only used content-based sizing | Added `min-height: 44px` to interactive controls |
| Previous-progress table could overflow the viewport | Tables need either a wider layout or a mobile transformation | Kept table format and placed it inside a horizontal scroll container |

### Files Modified

- `src/styles/global.css`: Added global overflow guards, media scaling, iframe sizing, mobile card spacing, touch-sized buttons, and heading wrapping.
- `src/features/routine/routine.css`: Added mobile-first sizing safeguards, responsive task controls, mobile typography, and scrollable modal table handling.
- `src/features/water/water-tracker.html`: Made the tracker panel fluid, buttons wrap, history rows stack, and mobile headings smaller.
- `src/features/feedback/feedback.html`: Added viewport metadata, fluid form width, touch-sized inputs, and mobile popup sizing.
- `src/features/workout/workout.html`: Added box sizing, iframe max-width handling, and mobile padding.
- `src/features/workout/PushUp.html`: Added stacked day rows on phones, touch-sized controls, and tablet/phone typography.
- `src/features/workout/maintenance.html`: Replaced fixed panel width with fluid sizing and added mobile padding/heading rules.
- `src/features/workout/prison.html`: Added mobile padding, touch-sized buttons, and wrapped history cards.

### Before And After Snippets

Global sizing:

```css
/* Before */
.container {
  max-width: 960px;
  margin: 50px auto;
  padding: 30px;
}
```

```css
/* After */
*,
*::before,
*::after {
  box-sizing: border-box;
}

.container {
  max-width: 960px;
  width: 100%;
  margin: 50px auto;
  padding: 30px;
}
```

Fixed form/card widths:

```css
/* Before */
.feedback-container {
  width: 350px;
}
```

```css
/* After */
.feedback-container {
  width: min(100%, 350px);
}
```

Mobile task layout:

```css
/* Before */
.task-item {
  display: flex;
  justify-content: space-between;
}
```

```css
/* After */
@media (max-width: 480px) {
  .task-item {
    align-items: stretch;
    flex-direction: column;
  }
}
```

Responsive iframe/card stack:

```css
/* Before */
.habit-frames {
  display: flex;
  justify-content: space-between;
}
```

```css
/* After */
@media (max-width: 768px) {
  .habit-frames {
    flex-direction: column;
    gap: 16px;
  }

  .habit-frame {
    width: 100%;
    height: 760px;
  }
}
```

### Summary Of Improvements

- Removed page-level horizontal scrolling risks across 320px, 375px, 425px, 768px, and desktop widths.
- Preserved the desktop layout while adding mobile and tablet overrides.
- Improved small-screen text hierarchy, card spacing, iframe scaling, and form/button usability.
- Kept tables usable on mobile with horizontal scrolling instead of forcing a cramped layout.
- Verified the home page with a headless 320px Edge screenshot after the responsive pass.

## Features

- Daily routine tracker with custom tasks.
- Holiday task tracking.
- Daily affirmations.
- Water intake tracker.
- 100-day push-up challenge.
- 365-day push-up maintenance tracker.
- Feedback form.
- MySQL database schema for scalable backend storage.
- Secure account registration, login, refresh-token flow, and protected profile APIs.

## Repository Structure

```text
MindMirror/
|-- index.html
|-- src/
|   |-- assets/
|   |-- features/
|   |   |-- feedback/
|   |   |-- routine/
|   |   |-- water/
|   |   `-- workout/
|   |-- shared/
|   `-- styles/
|-- backend/
|   |-- docs/
|   |-- src/main/java/com/mindmirror/backend/
|   |-- src/main/resources/db/migration/
|   `-- pom.xml
|-- docs/
|-- ARCHITECTURE.md
`-- README.md
```

See [Project Structure](docs/project-structure.md) for ownership rules and naming conventions.

## Quick Start

### Frontend

Open `index.html` directly in a browser, or run a simple local server from the repository root:

```powershell
python -m http.server 8000
```

Then open:

```text
http://localhost:8000/
```

### Backend

Start MySQL, then run:

```powershell
cd backend
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Health check:

```text
http://localhost:8081/api/health
```

Auth endpoints:

```text
POST http://localhost:8081/api/auth/register
POST http://localhost:8081/api/auth/login
POST http://localhost:8081/api/auth/refresh
POST http://localhost:8081/api/auth/logout

GET    http://localhost:8081/api/users/me
PUT    http://localhost:8081/api/users/me
DELETE http://localhost:8081/api/users/me

GET    http://localhost:8081/api/water?date=YYYY-MM-DD
PUT    http://localhost:8081/api/water
GET    http://localhost:8081/api/water/history
GET    http://localhost:8081/api/water/stats
```

## Documentation

- [Architecture](ARCHITECTURE.md)
- [Documentation Index](docs/README.md)
- [Project Structure](docs/project-structure.md)
- [Development Workflow](docs/development-workflow.md)
- [Scalability Roadmap](docs/scalability-roadmap.md)
- [Frontend Architecture](docs/frontend-architecture.md)
- [Backend Guide](backend/README.md)
- [Database Schema](backend/docs/database-schema.md)
- [API Roadmap](backend/docs/api-roadmap.md)
- [Contributing Guide](CONTRIBUTING.md)

## Database

Flyway migrations live in:

```text
backend/src/main/resources/db/migration/
```

Current migrations:

- `V1__initial_schema.sql`
- `V2__phase_2_core_schema.sql`
- `V3__seed_development_data.sql`
- `V4__auth_refresh_tokens.sql`

The schema includes users, refresh tokens, routine tasks, completions, water entries, push-up entries, maintenance entries, feedback entries, and affirmations.

## Development Principles

- Keep feature code grouped by domain under `src/features`.
- Keep shared browser utilities under `src/shared`.
- Keep backend code package-oriented by capability.
- Use Flyway for every database change.
- Prefer small, focused changes over broad rewrites.
- Update docs whenever architecture, setup, or data contracts change.

## Contributing

Before changing structure, database schema, or API contracts, read [CONTRIBUTING.md](CONTRIBUTING.md).

## Author

Bhupesh Baghel
