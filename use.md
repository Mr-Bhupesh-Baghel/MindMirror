# Using MindMirror

MindMirror runs like a normal website in your browser. Although it is built with **React** and **Spring Boot**, end users never interact with React, TypeScript, or `App.tsx`. They simply open the website and use its features.

---
## Local setup

# Development
# set password: ${DB_PASSWORD:your PostgreSQL password}  first
backend\src\main\resources\application.yaml    
# than   
## 1. Start the Backend

Open a terminal and run:

```bash
cd backend
mvn spring-boot:run
```

The backend API will start (default):

```
http://localhost:8082
```

---

## 2. Start the Frontend

Open another terminal and run:

```bash
cd frontend
npm run dev
```

The Vite development server will start (default):

```
http://localhost:5173
```

---

## 3. Open the Application

Visit the following URL in your browser:

```
http://localhost:5173
```

From there you can:

* Create a new account (Sign Up)
* Log in with your account
* Access the Dashboard
* View your Mind Tree
* Track habits, moods, and other MindMirror features

---

# Production Deployment

Before deploying, build the frontend:

```bash
cd frontend
npm run build
```

This creates a `dist/` directory containing the optimized production files.

Deploy the contents of the `dist/` folder to any static web server, such as:

* Nginx
* Apache
* Vercel
* Netlify
* Cloudflare Pages

The frontend communicates with the Spring Boot backend through its API. Users only see the website—they do not see React, TypeScript, Vite, or the project source code.

---

# Application Architecture

```text
                User
                  │
                  ▼
        Web Browser (Chrome, Edge, Firefox)
                  │
                  ▼
      React Frontend (http://localhost:5173)
                  │
             HTTP / REST API
                  │
                  ▼
 Spring Boot Backend (http://localhost:8082)
                  │
        Spring Data JPA / Hibernate
                  │
                  ▼
          PostgreSQL Database
```

---

# Running Checklist

Before using MindMirror, ensure:

* ✅ PostgreSQL is running
* ✅ Backend is running (`mvn spring-boot:run`)
* ✅ Frontend is running (`npm run dev`)
* ✅ Open `http://localhost:5173` in your browser
* ✅ Create an account or log in
* ✅ Start using MindMirror

Once all services are running, the application is ready to use just like any modern web application.
