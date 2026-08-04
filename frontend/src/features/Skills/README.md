# Adding a dashboard skill

1. Add an entry to `catalog.ts`. The `key` must be unique and use lowercase kebab-case.
2. Add the same key, lesson count, and order to `TreeService.SKILLS` in the backend. This lets the API persist progress and calculate unlocking.
3. Set `available: false` until its interactive lesson experience is ready. The dashboard will render it as coming soon automatically.

The dashboard, progress display, XP action, and locked/unlocked UI all read from the catalog and the API; no dashboard component needs editing for a new skill.
