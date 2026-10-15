# MindMirror backend services

The account backend is split into four independently deployable Spring Boot applications:

- `api-gateway` on port `8084` routes `/api/auth/**`, `/api/user/**`, and `/api/data/**`.
- `auth-service` on port `8081` owns registration, login, password hashes, and signed bearer tokens.
- `user-service` on port `8082` owns account profiles.
- `data-service` on port `8083` owns the per-user application data document and guest migrations.

All three domain services use the same MySQL database and must share `MINDMIRROR_TOKEN_SECRET`. Database settings are read from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; the checked-in defaults are for local development only.

From each service directory, run `mvnw.cmd spring-boot:run` (or `./mvnw spring-boot:run` on macOS/Linux). Start MySQL first, then the domain services, then the gateway.

The static frontend uses `http://localhost:8084/api` automatically when opened from `localhost` or a local file. For a deployed frontend, set `window.MINDMIRROR_API_BASE` before `src/shared/storage.js` loads, for example:

```html
<script>window.MINDMIRROR_API_BASE = "https://api.example.com/api";</script>
```
