### goal 
Frontend: GitHub Pages ✅

Backend: Oracle Cloud Free VM ✅

Database: MySQL ✅

Source Code: GitHub ✅


## checklist

- [x] Keep the static HTML/CSS/JavaScript frontend and its existing `localStorage` experience working.
- [x] Maintain the Spring Boot 3.3.5 backend on Java 17.
- [x] Keep MySQL schema changes under Flyway migrations (`V1`–`V5`); JPA validates rather than creates the production schema.
- [x] Provide API coverage for routines, water, push-ups, affirmations, feedback, accounts, migration/sync, and admin reporting.
- [x] Secure protected APIs with JWT access tokens, refresh tokens, BCrypt passwords, CORS configuration, security headers, and rate limiting.
- [x] Retain the account dashboard, one-time local-data migration, retry queue, duplicate prevention, and manual/online sync status.
- [x] Keep Docker Compose support for MySQL 8.4 and the backend, including persistent database storage and container health checks.
- [x] Keep production configuration environment-driven (`DB_*`, `JWT_*`, CORS, rate limits, and active Spring profile).
- [x] Keep Actuator health/readiness, Prometheus metrics, structured API errors, and backend tests.
- [x] Run the existing GitHub Actions workflow on `main` pushes and pull requests to test and package the backend.

- [ ] Connect the routine, water, workout, affirmation, and feedback screens directly to their APIs for normal daily use; keep `localStorage` as an offline fallback until that transition is complete.
- [ ] Add end-to-end coverage for registration, token refresh, migration/retry, sync, and each core tracker flow.
- [ ] Verify the deployed GitHub Pages site against the current frontend build and document its public URL.
- [ ] Verify the Oracle Cloud deployment: SSH access, backend container health, MySQL persistence, and the production CORS origin.
- [ ] Put the backend behind Nginx (or equivalent), configure HTTPS with a real certificate, and use a custom domain if one is chosen.
- [ ] Move all local/default secrets out of tracked configuration, store production values in the deployment environment, and rotate exposed development credentials.
- [ ] Schedule and test MySQL backups and a restore procedure; monitor disk space and database health.
- [ ] Add deployment automation after CI (image/build release, remote deployment, health-check rollback or notification).
Completed the Account feature end-to-end.

  - Rebuilt [account.html](C:\My Data\project\MindMirror\src\features\account\account.html) and added its working [account.js](C:\My
    Data\project\MindMirror\src\features\account\account.js): registration, login, profile/email/password updates, preferences, sync, browser backup import/
    export, logout, and account deletion.

  - Added persisted account preferences and summary statistics via [/api/users/me/summary](C:\My
    Data\project\MindMirror\backend\src\main\java\com\mindmirror\backend\user\UserController.java).

  - Added [V6 user-preferences migration](C:\My Data\project\MindMirror\backend\src\main\resources\db\migration\V6__user_preferences.sql).
  - Enhanced the shared API client with automatic refresh-token retry.
  - Replaced the broken legacy account.js.js reference with the new correctly named script.
  - Added backend account API coverage in [UserAccountControllerTest.java](C:\My
    Data\project\MindMirror\backend\src\test\java\com\mindmirror\backend\user\UserAccountControllerTest.java).

  Verification: mvn test passes — 14 tests, 0 failures.




  • Use MindMirror like this:

  1. Start the backend and database:

  docker compose up --build

  Or start MySQL yourself, then:

  cd backend
  & "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run

  2. Start the frontend from the project root:

  python -m http.server 8000

  3. Open:

  http://localhost:8000/

  4. Click the account icon, then:

  - Create an account with display name, email, and password.
  - Sign in with the same email and password.
  - Open My Account to update your profile, password, dark mode, and notifications.
  - Use Sync local data to upload existing browser-stored routine, water, push-up, affirmation, and feedback data.
  - Use Export browser backup before changing browsers/devices.
  - Use Import browser backup to restore an exported backup, then sync it.

  If your backend is hosted elsewhere, open My Account, enter its address under Backend API URL, save it, then sign in again.


 Fixed the registration 400 cause.

  - The frontend now requires the backend’s password policy: 8–128 characters with uppercase, lowercase, number, and symbol.
  - Validation errors from the API now display as readable messages instead of raw JSON.
  - Applied the same rule to profile password changes.

  Changed /C:/My Data/project/MindMirror/src/features/account/account.html and /C:/My Data/project/MindMirror/src/shared/api-client.js.

  Verified the live backend returns the expected validation message for a weak password, and both JavaScript files pass syntax checks. Maven
  tests couldn’t run because Maven/Maven Wrapper isn’t available.

   Completed the feedback flow end-to-end.

  - Rebuilt /C:/My Data/project/MindMirror/src/features/account/feedback.html as an accessible, responsive form.
  - Sends feedback to POST /api/feedback, including name, email, rating, message, and date.
  - Prefills name/email for signed-in users.
  - Validates inputs and shows API error messages.
  - If the server is unreachable, saves feedback locally for automatic sync after sign-in.
  - Fixed the broken home-page Feedback link in /C:/My Data/project/MindMirror/index.html.

  Verified the live backend returns its expected validation response; shared API client syntax check passes.