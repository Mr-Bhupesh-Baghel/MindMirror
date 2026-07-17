# MindMirror – Quick Start (Windows) (run MindMirror locally).

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

## 2. Start Backend

Open PowerShell:

```powershell
cd "C:\My Data\project\MindMirror\backend"
```

Run:

```powershell
& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

## Clean the Maven project (removes the target folder)

& "..\.tools\apache-maven-3.9.9\bin\mvn.cmd" clean

# Actual migration files (source)
dir .\src\main\resources\db\migration
# Compiled migration files (target)
dir .\target\classes\db\migration

### Check Backend

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
Copy and paste this into your browser's address bar (Chrome, Edge, Firefox):
```
http://localhost:8081/api/health
```

Expected:

```json
{"status":"UP"}
```

**Keep this terminal open.**

---

## 3. Start Frontend

Open another PowerShell:

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

### Check Frontend

Expected:

```text
Serving HTTP on :: port 8000
```

Open:

```
http://localhost:8000
```

**Keep this terminal open.**

---


## 6. Sign In

Enter:

* Email
* Password

Click **Sign In**.

Expected:

```
Signed in successfully.
```

---

# Stop MindMirror

Stop Backend:

```
Ctrl + C
```

Stop Frontend:

```
Ctrl + C
```

---


## Required Services

| Service  | Check                              |
| -------- | ---------------------------------- |
| MySQL    | `sc.exe query MySQL80`             |
| Backend  | `http://localhost:8081/api/health` |
| Frontend | `http://localhost:8000`            |

This covers the essential commands and checks needed to start and verify MindMirror locally.
