# Scalability Roadmap

This roadmap focuses on making MindMirror easier to extend without rewriting the whole project at once.

## Phase 1: Stabilize Structure

Status: in progress.

- Keep frontend features grouped by domain.
- Keep backend packages grouped by domain.
- Keep database schema under Flyway.
- Keep docs current with setup and architecture.
- Avoid committing generated or local machine files.

## Phase 2: Backend APIs

Build APIs one domain at a time:

- Users and authentication.
- Routine tasks and completions.
- Water entries.
- Push-up entries.
- Maintenance entries.
- Feedback entries.
- Affirmations.

Use `/api/v1` for new endpoints.

## Phase 3: Data Migration

Move from browser-only persistence to backend persistence.

Recommended flow:

1. Read existing `localStorage` data.
2. Convert it to API request payloads.
3. Upload data after login.
4. Mark migrated keys after successful sync.
5. Keep export/backup options until migration is trusted.

## Phase 4: Reliability

- Add global exception handling.
- Add validation on all request DTOs.
- Add integration tests for migrations and endpoints.
- Add structured logging.
- Add environment-specific profiles.

## Phase 5: Production Readiness

- Add JWT or session-based authentication.
- Add password hashing and account recovery.
- Add rate limiting for public endpoints.
- Add database backups.
- Add deployment documentation.
- Add monitoring and health checks beyond `SELECT 1`.

## Phase 6: Advanced Modules

- Admin analytics.
- Desktop app wrapper.
- AI assistant integrations.
- OCR.
- Voice assistant.

## Design Principle

Scale by adding clear boundaries first:

- Feature boundaries in frontend code.
- Domain boundaries in backend code.
- Migration boundaries in database changes.
- Documentation boundaries for setup, architecture, and API contracts.
