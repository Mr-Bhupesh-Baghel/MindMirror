# MindMirror

> **Track your habits. Protect your mind.**

MindMirror is a lightweight, offline-first personal wellness and productivity application designed to help users build healthy habits, track progress, and maintain daily routines without requiring an internet connection or user accounts.

The project currently runs entirely in the browser and stores all data locally using `localStorage`.

---

# Features

## Daily Routine Management

* Daily task tracker
* Custom task creation
* Holiday task management
* Progress tracking
* Previous-day history

## Habit Building

* Daily affirmations
* 100-Day Push-Up Challenge
* 365-Day Push-Up Maintenance Tracker
* Water intake tracker (8 glasses per day)

## Feedback System

* Local feedback form
* Offline data storage

## Privacy First

* No account required
* No cloud storage
* No tracking
* No analytics
* Works completely offline

---

# Technology Stack

## Frontend

* HTML5
* CSS3
* Vanilla JavaScript

## Storage

* Browser `localStorage`

## External Libraries

* SheetJS/XLSX (Excel Export)

---

# Current Architecture

```text
Browser
│
├── Dashboard
├── Routine Tracker
├── Push-Up Tracker
├── Water Tracker
└── Feedback System
        │
        ▼
Browser localStorage
```

---

# Project Structure

```text
MindMirror/
│
├── index.html
├── README.md
├── ARCHITECTURE.md
│
├── src/
│   ├── assets/
│   ├── styles/
│   ├── shared/
│   └── features/
│       ├── routine/
│       ├── pushups/
│       ├── water/
│       └── feedback/
│
└── .gitignore
```

---

# Getting Started

## Option 1: Open Directly

Simply open:

```text
index.html
```

in your browser.

---

## Option 2: Run a Local Development Server (Recommended)

Start a Python server:

```powershell
python -m http.server 8000
```

Then open:

```text
http://localhost:8000/
```

---

# Data Storage

MindMirror stores all data locally inside your browser using `localStorage`.

Stored data includes:

* Daily tasks
* Custom tasks
* Affirmations
* Push-up progress
* Water intake history
* Feedback entries

⚠️ Clearing browser data will permanently remove saved progress.

---

# Current Limitations

* No user accounts
* No cloud synchronization
* No backend API
* No database
* No authentication
* No multi-device support
* No automated backups

---

# Project Status

Current Status:

```text
Prototype + Early Full-Stack Development
```

Architecture Type:

```text
Offline-First Browser Application
```

Future Direction:

```text
Spring Boot Backend
→ MySQL Database
→ Authentication System
→ Desktop Application
→ AI Integration
→ Voice Assistant
```

---
Frontend : ~45% complete
Backend  : ~15% complete
Overall  : ~30% complete
# Author

**Bhupesh Baghel**

