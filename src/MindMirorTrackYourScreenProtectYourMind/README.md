# MindMirror

Track your screen. Protect your mind.

MindMirror is a lightweight, offline-first digital wellness prototype for tracking daily routines, push-up habits, water intake, and local feedback.

## Current Features

- Daily routine tracker
- Custom daily tasks
- Holiday tasks
- Daily affirmations
- 100-day push-up challenge
- 365-day push-up maintenance tracker
- 8-glasses water intake tracker
- Local feedback form

## Tech Stack

- HTML
- CSS
- Plain JavaScript
- Browser `localStorage`
- SheetJS/XLSX CDN for Excel export

There is no backend, database, build step, package manager, or install step.

## Run Locally

Open `index.html` directly in a browser.

For more predictable iframe and CDN behavior, run a static server from the repository root:

```powershell
python -m http.server 8000
```

Then open:

```text
http://localhost:8000/
```

## Project Structure

```text
src/
  assets/
  styles/
  shared/
  features/
    routine/
    pushups/
    water/
    feedback/
```

## Data Storage

All user data is saved in browser `localStorage`. Clearing browser storage deletes saved progress.

See `ARCHITECTURE.md` for the full audit, data map, and roadmap.
