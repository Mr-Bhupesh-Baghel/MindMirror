# Development Workflow

## Local Frontend

From the repository root:

```powershell
python -m http.server 8000
```

Open:

```text
http://localhost:8000/
```

The frontend currently stores data in browser `localStorage`.

## Local Backend

Start MySQL, then run from `backend/`:

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Health endpoint:

```text
GET http://localhost:8081/api/health
```

## Tests

From `backend/`:

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" test
```

Current backend tests require MySQL because Flyway runs on application startup.

## Change Checklist

Before finishing a change:

- Run relevant tests or explain why they could not be run.
- Check `git status --short`.
- Update docs if setup, architecture, migrations, or contracts changed.
- Keep generated files out of commits.

## Database Change Checklist

- Add a new Flyway migration.
- Include foreign keys and indexes with table changes.
- Add or update seed data only when it is safe for development.
- Update `backend/docs/database-schema.md`.
- Run backend tests with MySQL available.

## Frontend Change Checklist

- Keep feature code in the feature folder.
- Prefer ISO dates for new data.
- Use `src/shared/storage.js` instead of direct `localStorage` for new local persistence.
- Avoid introducing duplicate utility functions across pages.
- Test through the browser page that owns the feature.
