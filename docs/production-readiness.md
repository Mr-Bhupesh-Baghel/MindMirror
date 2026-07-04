# Production Readiness

Phase 10 prepares the Spring Boot backend for real users with operational defaults, safer configuration, automated tests, and deployment scaffolding.

## Runtime Profiles

- Default profile: local development with MySQL on `localhost:3306`.
- `test` profile: H2 in-memory database, Flyway disabled, rate limiting disabled.
- `prod` profile: production logging, strict secret validation, actuator health details hidden.

Run production locally:

```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:mysql://localhost:3306/mindmirror?useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME="mindmirror"
$env:DB_PASSWORD="replace-with-db-password"
$env:JWT_SECRET="replace-with-at-least-32-random-characters"
$env:APP_CORS_ALLOWED_ORIGINS="https://your-frontend.example"
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

## Stability And Monitoring

- Global exception handling returns consistent JSON for validation, malformed body, unsupported method/media type, data conflicts, and unhandled errors.
- Application logs unhandled and data integrity failures.
- Health endpoints:
  - `GET /api/health`
  - `GET /actuator/health`
  - `GET /actuator/health/liveness`
  - `GET /actuator/health/readiness`
- Metrics endpoints:
  - `GET /actuator/info`
  - `GET /actuator/metrics`
  - `GET /actuator/prometheus`

Metrics endpoints require an admin JWT unless explicitly permitted later by infrastructure policy.

## Security

- CORS is controlled by `APP_CORS_ALLOWED_ORIGINS`.
- Rate limiting is controlled by `APP_RATE_LIMIT_ENABLED` and `APP_RATE_LIMIT_REQUESTS_PER_MINUTE`.
- Secure headers include frame denial, content type sniffing protection, no-referrer policy, HSTS, and API-only content security policy.
- Production startup fails if `JWT_SECRET` or `DB_PASSWORD` uses development defaults.

## Testing

Run:

```powershell
cd backend
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" test
```

The suite covers:

- Spring context startup.
- Health API and database probe.
- Validation error response shape.
- Secure response headers.
- Rate limit rejection behavior.
- Existing domain service tests.

## Deployment

Build backend image:

```powershell
docker build -t mindmirror-backend ./backend
```

Run backend plus MySQL:

```powershell
$env:JWT_SECRET="replace-with-at-least-32-random-characters"
$env:DB_PASSWORD="replace-with-db-password"
$env:MYSQL_ROOT_PASSWORD="replace-with-root-password"
docker compose up --build
```

CI is defined in `.github/workflows/backend-ci.yml` and runs Maven tests plus packaging on pushes and pull requests to `main`.

## Database Backup Strategy

Use MySQL logical dumps before deployments and on a scheduled daily cadence:

```powershell
docker compose exec mysql mysqldump -u root -p mindmirror > backups/mindmirror-%DATE%.sql
```

Recommended production policy:

- Daily encrypted backup retained for 30 days.
- Weekly encrypted backup retained for 12 weeks.
- Monthly encrypted backup retained for 12 months.
- Restore test at least once per month.
- Backup before every Flyway migration deployment.
