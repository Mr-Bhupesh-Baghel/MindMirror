# Project Structure

MindMirror is organized as a hybrid frontend and backend repository.

## Root

```text
MindMirror/
|-- index.html
|-- src/
|-- backend/
|-- docs/
|-- videos/
|-- README.md
`-- ARCHITECTURE.md
```

Root files should stay small and navigational. Detailed guidance belongs in `docs/` or `backend/docs/`.

## Frontend

```text
src/
|-- assets/
|-- features/
|-- shared/
`-- styles/
```

### `src/features`

Feature folders own user-facing pages and feature-specific code.

Current features:

- `feedback`
- `routine`
- `water`
- `workout`

Naming rules:

- Use lowercase folder names.
- Prefer kebab-case file names for new HTML/CSS/JS files.
- Keep files close to the feature that owns them.
- Move reusable code to `src/shared` only after at least two features need it.

### `src/shared`

Shared browser utilities live here. Current utility:

- `storage.js`: wrapper around `localStorage`.

Future candidates:

- API client.
- Date formatting.
- Form validation helpers.
- UI notification helpers.

### `src/styles`

Global styles only. Feature-specific styles should live beside the feature.

## Backend

```text
backend/
|-- docs/
|-- src/main/java/com/mindmirror/backend/
|-- src/main/resources/
|-- src/test/
`-- pom.xml
```

Backend package growth should be domain-first:

```text
com.mindmirror.backend.routine
com.mindmirror.backend.water
com.mindmirror.backend.workout
com.mindmirror.backend.feedback
com.mindmirror.backend.user
com.mindmirror.backend.auth
```

Each domain should own its controller, service, repository, DTOs, and entities.

## Database Migrations

```text
backend/src/main/resources/db/migration/
```

Rules:

- Use Flyway versioned migrations.
- Do not edit migrations already applied to any shared database.
- Add indexes and constraints in the same migration as the table when possible.
- Put seed data in a separate migration from schema changes.

## Generated and Local Files

These should not be committed:

- `.m2/`
- `.tools/`
- `target/`
- IDE folders
- `.env` files
- logs and temporary files
