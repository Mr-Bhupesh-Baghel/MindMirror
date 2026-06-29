# Contributing

This guide keeps MindMirror changes readable and scalable.

## Before You Start

- Read `README.md` for setup.
- Read `ARCHITECTURE.md` before changing boundaries.
- Check `docs/project-structure.md` before adding files or folders.
- Check `backend/docs/database-schema.md` before changing data models.

## Code Organization

- Keep frontend code grouped by feature under `src/features`.
- Keep shared frontend helpers under `src/shared`.
- Keep backend code grouped by domain package.
- Keep database changes in Flyway migrations.
- Do not mix unrelated refactors with feature work.

## Documentation

Update documentation when changing:

- Setup commands.
- Folder structure.
- Database schema.
- API contracts.
- Authentication or security behavior.
- Development workflow.

## Database Changes

- Add a new migration instead of editing an applied migration.
- Name migrations with Flyway format: `V<number>__description.sql`.
- Keep schema changes and large seed changes separate.
- Add indexes for expected query paths.
- Add constraints for data rules the database can enforce.

## Frontend Changes

- Prefer feature-specific files over growing large inline scripts.
- Use `src/shared/storage.js` for local persistence.
- Use ISO dates for new data that may sync with the backend.
- Avoid duplicating utilities across feature pages.

## Backend Changes

- Controllers should handle HTTP concerns.
- Services should hold business rules.
- Repositories should hold persistence access.
- DTOs should define API contracts.
- Entities should not be exposed directly as API responses.
- Add validation before accepting user input.

## Verification

Run the smallest useful checks for the change:

```powershell
git diff --check
```

For backend changes:

```powershell
cd backend
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" test
```

If a check cannot be run, document why.
