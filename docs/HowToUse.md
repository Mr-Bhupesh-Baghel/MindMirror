# 🚀 Quick Start

## Prerequisites

Install the following before running MindMirror:

* Java 17
* Maven 3.9+
* MySQL Community Server 8.0
* Git
* VS Code (recommended)
* Postman (recommended)

---

# 1. Verify MySQL

Open PowerShell and connect to MySQL:

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

Enter your MySQL password.

Verify the database exists:

```sql
SHOW DATABASES;
```

You should see:

```text
mindmirror
```

Exit MySQL:

```sql
exit;
```

---

# 2. Configure Environment

Create a `.env` file inside the `backend` directory.

Example:

```env
DB_URL=jdbc:mysql://localhost:3306/mindmirror?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=YOUR_MYSQL_PASSWORD

SERVER_PORT=8081

JWT_SECRET=replace-with-at-least-32-random-characters

JWT_ACCESS_TOKEN_TTL=15m
JWT_REFRESH_TOKEN_TTL=30d
```

---

# 3. Start Backend

```powershell
cd backend
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Wait until you see:

```text
Started MindMirrorBackendApplication
```

---

# 4. Verify Backend

PowerShell:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/health
```

Expected:

```json
{
  "status": "UP"
}
```

or

```powershell
curl http://localhost:8081/actuator/health
```

Expected HTTP Status:

```
200 OK
```

---

# 5. API Testing (Postman)

## Register

POST

```
http://localhost:8081/api/auth/register
```

Headers

```
Content-Type: application/json
```

Body

```json
{
  "email": "bhupesh@example.com",
  "password": "Password@123",
  "displayName": "Bhupesh Baghel"
}
```

---

## Login

POST

```
http://localhost:8081/api/auth/login
```

Body

```json
{
  "email": "bhupesh@example.com",
  "password": "Password@123"
}
```

Save the JWT access token returned by the API.

---

## Protected Endpoint

GET

```
http://localhost:8081/api/users/me
```

Header

```
Authorization: Bearer <JWT_TOKEN>
```

---

# 6. Frontend

Run a local server:

```powershell
python -m http.server 8000
```

Open:

```
http://localhost:8000
```

---

# 7. Development Workflow

```
Start MySQL
      ↓
Start Spring Boot
      ↓
Verify Health API
      ↓
Register User
      ↓
Login
      ↓
Get JWT Token
      ↓
Test Protected APIs
      ↓
Connect Frontend
      ↓
Deploy
```

---

# 8. API Groups

## Authentication

* POST `/api/auth/register`
* POST `/api/auth/login`
* POST `/api/auth/refresh`
* POST `/api/auth/logout`

## User

* GET `/api/users/me`
* PUT `/api/users/me`
* DELETE `/api/users/me`

## Routine

* GET `/api/routine/tasks`
* POST `/api/routine/tasks`
* PATCH `/api/routine/tasks/{id}`
* DELETE `/api/routine/tasks/{id}`
* GET `/api/routine/completions`
* PUT `/api/routine/completions`
* GET `/api/routine/history`
* GET `/api/routine/history/export`

## Water

* GET `/api/water`
* PUT `/api/water`
* GET `/api/water/history`
* GET `/api/water/stats`

## Push-ups

* GET `/api/pushups/challenge`
* PUT `/api/pushups/challenge`
* GET `/api/pushups/challenge/history`
* GET `/api/pushups/maintenance`
* PUT `/api/pushups/maintenance`
* GET `/api/pushups/maintenance/history`

## Affirmations

* GET `/api/affirmations`
* POST `/api/affirmations`
* DELETE `/api/affirmations/{id}`

## Sync

* POST `/api/migration/start`
* GET `/api/migration/status`
* POST `/api/sync`
* GET `/api/sync/status`

## Admin

Requires an ADMIN JWT.

* GET `/api/admin/users`
* PATCH `/api/admin/users/{id}`
* DELETE `/api/admin/users/{id}`
* GET `/api/admin/feedback`
* GET `/api/admin/stats`
* GET `/api/admin/export?dataset=users&format=csv`
* GET `/api/admin/export?dataset=feedback&format=xlsx`
