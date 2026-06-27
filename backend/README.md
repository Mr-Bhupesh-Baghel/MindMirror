# MindMirror Backend

> Spring Boot REST API powering the MindMirror application.

The MindMirror Backend provides REST APIs for habit tracking, user management, water tracking, push-up challenges, and future AI integrations.

---

# Features

* RESTful API architecture
* Spring Boot 3 application
* MySQL database integration
* Environment-based configuration
* Health Check API
* Maven build system
* Ready for authentication and AI modules

---

# Technology Stack

| Technology      | Version |
| --------------- | ------- |
| Java            | 21      |
| Spring Boot     | 3.x     |
| Spring Data JPA | Latest  |
| MySQL           | 8+      |
| Maven           | 3.9+    |
| Hibernate       | Latest  |
| HikariCP        | Latest  |

---

# Requirements

Before running the application, install:

| Software | Version               |
| -------- | --------------------- |
| Java     | 17+ (Recommended: 21) |
| Maven    | 3.9+                  |
| MySQL    | 8+                    |
| Git      | Latest                |

---

# Project Structure

```text
backend/
│
├── src/
│   ├── main/
│   │   ├── java/com/mindmirror/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── config/
│   │   │   ├── exception/
│   │   │   └── util/
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

---

# Environment Variables

⚠️ Never commit your real database password to GitHub.

## Windows PowerShell

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/mindmirror?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:SERVER_PORT="8081"
```

## Windows CMD

```cmd
set DB_URL=jdbc:mysql://localhost:3306/mindmirror?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set DB_USERNAME=root
set DB_PASSWORD=your_password
set SERVER_PORT=8081
```

---

# Database Configuration

```text
Database Name : mindmirror
Port          : 3306
Driver        : MySQL Connector/J
ORM           : Hibernate (JPA)
Connection Pool : HikariCP
```

The database will be created automatically if it does not already exist.

---

# Running the Application

## 1. Navigate to Backend Directory

```powershell
cd "C:\My Data\project\MindMirror\backend"
```

---

## 2. Start the Application

### Using Maven

```powershell
mvn spring-boot:run
```

### Using Local Maven Installation

```powershell
& "C:\My Data\project\MindMirror\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

---

## 3. Successful Startup

If everything starts correctly, you should see:

```text
Started MindMirrorBackendApplication
Tomcat started on port(s): 8081
```

---

# Verify Application Health

Open:

```text
http://localhost:8081/api/health
```

or run:

```powershell
curl http://localhost:8081/api/health
```

Expected response:

```json
{
  "status": "UP",
  "database": true,
  "timestamp": "2026-06-18T00:00:00Z"
}
```

---

# Stopping the Application

Press:

```text
Ctrl + C
```

Then:

```text
Terminate batch job (Y/N)?
```

Type:

```text
Y
```

and press Enter.

---

# Checking Application Status

## Running

```powershell
curl http://localhost:8081/api/health
```

Response:

```json
{
  "status": "UP",
  "database": true
}
```

## Stopped

```text
Unable to connect to the remote server
```

---

# API Architecture

```text
Client
   │
   ▼
REST Controller
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
MySQL Database
```

---

# Current Modules

* Health Check API
* Database Connectivity
* Configuration Management

---

# Planned Modules

* Authentication & Authorization
* User Management
* Routine Management
* Water Tracker APIs
* Push-Up Tracker APIs
* Feedback APIs
* AI Integration APIs
* OCR APIs
* Voice Assistant APIs

---

# Development Status

```text
Phase 1 : Backend Foundation        ✅
Phase 2 : Database Integration      🚧
```

---

# Security Roadmap

* JWT Authentication
* BCrypt Password Encryption
* Input Validation
* Global Exception Handling
* API Rate Limiting
* Role-Based Access Control (RBAC)

---

# Author

**Bhupesh Baghel**

---
