# MindMirror – Quick Start (Windows)

Run MindMirror locally on Windows.

---

## Prerequisites

Verify the required tools are installed:

```powershell
java -version
mvn -version
python --version
mysql --version
```

---

## 1. Start MySQL

### Check if MySQL is running

```powershell
sc.exe query MySQL80
```

Expected:

```text
STATE : 4 RUNNING
```

If it is not running:

```powershell
net start MySQL80
```

---

## 2. Start the Backend

Open a new PowerShell window:

```powershell
cd "C:\My Data\project\MindMirror\backend"
```

Run the application:

```powershell
mvn spring-boot:run
```

### Optional: Clean the project

```powershell
mvn clean
```

### Run all tests

```powershell
mvn test
```

### Verify the project

```powershell
mvn clean verify
```

### Check Flyway migration files

Source migrations:

```powershell
dir .\src\main\resources\db\migration
```

Compiled migrations:

```powershell
dir .\target\classes\db\migration
```

### Verify Backend Startup

Wait until you see:

```text
Started MindMirrorBackendApplication
Tomcat started on port 8081
```

Backend URL:

```
http://localhost:8081
```

Health Check:

Open:

```
http://localhost:8081/api/health
```

Expected:

```json
{"status":"UP"}
```

Keep this terminal open.

---

## 3. Start the Frontend

Open another PowerShell window:

```powershell
cd "C:\My Data\project\MindMirror"
```

Run:

```powershell
python -m http.server 8000
```

If Python is not found:

```powershell
py -m http.server 8000
```

### Verify Frontend

Expected:

```text
Serving HTTP on 0.0.0.0 port 8000
```

or

```text
Serving HTTP on :: port 8000
```

Open:

```
http://localhost:8000
```

Keep this terminal open.

---

## 4. Sign In

Open:

```
http://localhost:8000
```

Sign in using your registered account.

Expected:

```text
Signed in successfully.
```

---

# Stop MindMirror

Stop the backend:

```text
Ctrl + C
```

Stop the frontend:

```text
Ctrl + C
```

---

# Useful Maven Commands

```powershell
mvn clean
```

Delete the `target` directory.

```powershell
mvn compile
```

Compile the project.

```powershell
mvn test
```

Run unit tests.

```powershell
mvn clean verify
```

Clean, compile, run tests, and verify the project.

```powershell
mvn package
```

Create the executable JAR.

```powershell
mvn spring-boot:run
```

Run the Spring Boot application.

---

# Required Services

| Service | Verification |
|----------|--------------|
| Java | `java -version` |
| Maven | `mvn -version` |
| MySQL | `sc.exe query MySQL80` |
| Backend | `http://localhost:8081/api/health` |
| Frontend | `http://localhost:8000` |

---

This guide covers everything needed to build, test, run, and verify MindMirror on a Windows development machine.