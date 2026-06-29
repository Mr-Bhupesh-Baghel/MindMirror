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
| Backend foundation | Spring Boot app with health endpoint |
| Database foundation | Flyway migrations for core tables |
| Authentication | Planned |
| API integration from frontend | Planned |

## Features

- Daily routine tracker with custom tasks.
- Holiday task tracking.
- Daily affirmations.
- Water intake tracker.
- 100-day push-up challenge.
- 365-day push-up maintenance tracker.
- Feedback form.
- MySQL database schema for scalable backend storage.

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

The schema includes users, routine tasks, completions, water entries, push-up entries, maintenance entries, feedback entries, and affirmations.

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
