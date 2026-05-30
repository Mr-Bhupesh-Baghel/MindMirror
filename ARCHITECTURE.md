# MindMirror Architecture Report

## 1. Project Overview

MindMirror is a static, browser-based digital wellness prototype. It does not use a backend, package manager, build step, routing framework, database, or authentication layer. The application runs directly from HTML files and stores user progress in the browser through `localStorage`.

The current product surface is a wellness dashboard with these modules:

- Daily routine tracker
- Custom daily tasks
- Holiday tasks
- Daily affirmations
- Push-up challenge trackers
- Water intake tracker
- Feedback form

The home page acts as a dashboard shell and embeds some modules with `iframe` elements. Each tracker is mostly self-contained, with its own HTML, styling, and inline or linked JavaScript.

## 2. Technology Stack

The project uses:

- HTML5 for page structure
- CSS for styling
- Plain JavaScript for interaction and persistence
- Browser `localStorage` for all saved data
- SheetJS/XLSX from CDN for exporting routine progress to Excel
- Static image asset: `Logo.png`
- Optional Postman workspace metadata under `.postman/` and `postman/`

There is no `package.json`, dependency lockfile, transpiler, bundler, module loader, server runtime, or test framework.

## 3. Repository Structure

```text
.
|-- .gitattributes
|-- .postman/
|   `-- resources.yaml
|-- postman/
|   `-- globals/
|       `-- workspace.globals.yaml
|-- FeedBack/
|   `-- Feedback.html
|-- PushUp/
|   |-- MaintainPushUp365Day.html
|   `-- pushup100.html
|-- Routine/
|   |-- HolidayTask.js
|   |-- Routine.css
|   |-- Routine.html
|   `-- Routine.js
|-- Water/
|   `-- water8GlassesEveryDay.html
|-- Logo.png
|-- README.md
|-- index.html
`-- style.css
```

## 4. File Responsibilities

### Root Files

`index.html`

- Main application entry point.
- Displays the MindMirror logo, title, tagline, dashboard cards, feedback link, and footer.
- Embeds `Routine/Routine.html` as a small top-bar preview.
- Links to the full routine tracker page.
- Embeds `PushUp/pushup100.html` and `Water/water8GlassesEveryDay.html` side by side using iframes.
- Links to `FeedBack/Feedback.html`.

`style.css`

- Global styling for the home page.
- Defines page layout, cards, buttons, responsive breakpoints, and iframe styling helper class.
- Contains some styles that are not currently wired to the markup, such as `.dashboard` and `.frame`.

`README.md`

- Product-level description of MindMirror.
- Explains the problem, target users, current progress, market idea, and future revenue model.
- This is not a technical setup document.

`Logo.png`

- Main brand/logo image used by `index.html`.

`.gitattributes`

- Enables automatic text file detection and line-ending normalization.

### Routine Module

`Routine/Routine.html`

- Standalone page for daily routine tracking.
- Loads `Routine.css`, `Routine.js`, `HolidayTask.js`, and the SheetJS/XLSX CDN script.
- Defines the required DOM containers used by JavaScript:
  - `#progress`
  - `#affirmationList`
  - `#HtaskContainer`
  - `#taskContainer`
  - `#customTaskInput`
  - `#HolidayTaskInput`
  - `#affirmationInput`
  - `#addBtn`
  - `#clearAllBtn`
- Exposes buttons through inline `onclick` handlers for submit, history view, Excel export, data deletion, custom task creation, and holiday task creation.

Important note: this file references `pushup.js`, but no `Routine/pushup.js` file exists in the repository. This creates a 404 in the browser. The missing script does not appear necessary for the current routine tracker because push-up tracking is implemented separately under `PushUp/`.

`Routine/Routine.css`

- Styles the routine tracker page.
- Handles body layout, task cards, task rows, progress bar, inputs, buttons, and large task text.
- Defines `.H`, although the home page uses `.H` from `style.css` context where no `.H` rule exists.

`Routine/Routine.js`

- Core daily routine logic.
- Defines default task sections.
- Builds task UI dynamically into `#taskContainer`.
- Persists daily completion state in `localStorage` using date-based keys such as `daily-tasks-2026-05-30`.
- Stores custom tasks under `customTasks`.
- Calculates and updates progress percentage.
- Supports adding, deleting, and reordering custom tasks.
- Supports viewing previous daily progress in a dynamically created modal.
- Supports deleting daily progress keys.
- Supports moving to the next day.
- Exports daily completion percentages to `Performance.xlsx` using the global `XLSX` object from the CDN dependency.
- Manages daily affirmations under the `affirmations` key.

`Routine/HolidayTask.js`

- Manages holiday task UI inside `#HtaskContainer`.
- Stores holiday task text under `holidayTasks`.
- Stores checked holiday task names under `holidayTasksChecked`.
- Supports add, delete, and checkbox toggle behavior.

### Push-Up Module

`PushUp/pushup100.html`

- Standalone 100-day push-up tracker.
- Embeds `MaintainPushUp365Day.html` at the top as a nested iframe.
- Tracks challenge days, target count, actual completed count, date, attempt log, total pushups, average pushups, and last activity.
- Stores progress in `localStorage` under `pushupProgress`.
- Uses inline CSS and inline JavaScript.

`PushUp/MaintainPushUp365Day.html`

- Standalone 365-day maintenance tracker.
- Tracks days completed toward a 365-day goal.
- Records daily push-up capacity records.
- Stores completed day count under `completedDays`.
- Stores daily capacity records under `maintenanceRecords`.
- Uses inline CSS and inline JavaScript.

### Water Module

`Water/water8GlassesEveryDay.html`

- Standalone daily water intake tracker.
- Tracks progress toward 8 glasses per day.
- Displays progress using a CSS `conic-gradient` circle.
- Supports add, remove, view history, delete date, and delete all behavior.
- Stores all date-wise water progress in `localStorage` under `history`.

Note: `history` is a generic key name. If the project grows, this should be renamed to a namespaced key such as `mindmirror.water.history` to avoid collisions.

### Feedback Module

`FeedBack/Feedback.html`

- Standalone local feedback form.
- Captures name, email, star rating, message, and current date.
- Stores feedback entries under `feedbackList` in `localStorage`.
- Shows a thank-you popup after saving.
- Uses inline CSS and inline JavaScript.

No data is sent to a server. Feedback remains only in the user's browser.

### Postman Metadata

`.postman/resources.yaml`

- Registers local Postman workspace metadata.
- Points Postman Local View at resources under `postman/`.

`postman/globals/workspace.globals.yaml`

- Empty Postman globals file.
- Not used by the application runtime.

## 5. Dependency Model

Most dependencies are built into the browser:

- DOM APIs
- Events
- `localStorage`
- `Date`
- `JSON`
- `alert` and `confirm`
- `iframe`

The only external runtime dependency is:

```html
<script src="https://cdn.jsdelivr.net/npm/xlsx/dist/xlsx.full.min.js"></script>
```

This is loaded by `Routine/Routine.html` and provides the global `XLSX` object used by `exportToExcel()`.

Because this dependency is loaded from a CDN, Excel export requires network access when the page is opened, unless the script has already been cached by the browser. The rest of the project can run offline.

## 6. Runtime and Execution Flow

### Home Page Flow

1. User opens `index.html`.
2. Browser loads `style.css` and `Logo.png`.
3. Browser renders the dashboard shell.
4. Browser loads these iframe pages:
   - `Routine/Routine.html`
   - `PushUp/pushup100.html`
   - `Water/water8GlassesEveryDay.html`
5. Each iframe has its own document context but shares the same origin when opened from the same project location.
6. All modules read and write to the same browser `localStorage` namespace.

### Routine Tracker Flow

1. `Routine/Routine.html` loads.
2. `Routine.js` initializes:
   - Computes today's date.
   - Creates `storageKey` as `daily-tasks-{YYYY-MM-DD}`.
   - Calls `autoReset()`.
   - Calls `loadTasks()`.
   - Calls `loadStatus()`.
   - Starts `setInterval(autoReset, 60 * 1000)`.
3. `loadTasks()` renders default tasks and saved custom tasks.
4. `loadStatus()` applies saved checkbox states for the current day.
5. `updateProgress()` calculates completion percentage and writes current state to `localStorage`.
6. User actions update local state and then re-render or save:
   - Add custom task
   - Delete custom task
   - Move custom task
   - Check/uncheck task
   - Submit and next day
   - View previous days
   - Export Excel
   - Delete daily progress
   - Add/delete affirmations
7. `HolidayTask.js` separately initializes holiday tasks and writes holiday state to `localStorage`.

### Push-Up Tracker Flow

1. `PushUp/pushup100.html` loads.
2. Browser loads nested iframe `MaintainPushUp365Day.html`.
3. `pushup100.html` reads `pushupProgress`.
4. `renderTracker()` builds day cards and challenge summary.
5. User enters push-up count and clicks Done.
6. `markDone(day)` writes the record to `pushupProgress`.
7. UI re-renders the tracker, attempt log, and summary.

For the nested 365-day tracker:

1. `MaintainPushUp365Day.html` reads `completedDays` and `maintenanceRecords`.
2. It updates the progress bar.
3. User can increment a maintained day or add a daily capacity record.
4. Data is written back to `localStorage`.

### Water Tracker Flow

1. `Water/water8GlassesEveryDay.html` loads.
2. `window.onload = loadToday`.
3. `loadToday()` reads the `history` object from `localStorage`.
4. It extracts today's value using `new Date().toLocaleDateString()`.
5. Add/remove buttons update the count and call `saveProgress()`.
6. View History builds a date-wise list from the saved `history` object.
7. Delete actions remove one date or all water history.

### Feedback Flow

1. `FeedBack/Feedback.html` loads.
2. Existing `feedbackList` is read from `localStorage`.
3. User fills name, email, rating, and message.
4. Submit validates all fields.
5. A feedback object is appended to `feedbackList`.
6. Form is cleared.
7. Thank-you popup is displayed.

## 7. UI Flow

The user-facing navigation is simple:

```text
index.html
|-- Routine preview iframe
|-- Open Daily Routine Tracker link
|   `-- Routine/Routine.html
|-- Push-up iframe
|   `-- PushUp/pushup100.html
|       `-- nested iframe: PushUp/MaintainPushUp365Day.html
|-- Water iframe
|   `-- Water/water8GlassesEveryDay.html
`-- Feedback link
    `-- FeedBack/Feedback.html
```

The main dashboard mixes two interaction styles:

- Embedded modules can be used directly from `index.html`.
- Full-page modules can be opened through links.

The routine tracker is both embedded and linked. In the embedded version, its iframe height is only `110`, so only the top portion is visible. The full tracker experience is available through the Open button.

## 8. Data Storage Map

All data is saved in browser `localStorage`.

| Key | Owner | Purpose |
| --- | --- | --- |
| `daily-tasks-{YYYY-MM-DD}` | `Routine/Routine.js` | Checkbox state for a specific day |
| `customTasks` | `Routine/Routine.js` | User-created daily tasks |
| `lastOpenedDate` | `Routine/Routine.js` | Date used for daily reset behavior |
| `affirmations` | `Routine/Routine.js` | Saved daily affirmations |
| `holidayTasks` | `Routine/HolidayTask.js` | Holiday task list |
| `holidayTasksChecked` | `Routine/HolidayTask.js` | Checked holiday tasks |
| `pushupProgress` | `PushUp/pushup100.html` | 100-day push-up progress records |
| `completedDays` | `PushUp/MaintainPushUp365Day.html` | 365-day maintenance count |
| `maintenanceRecords` | `PushUp/MaintainPushUp365Day.html` | Push-up capacity records |
| `history` | `Water/water8GlassesEveryDay.html` | Date-wise water intake history |
| `feedbackList` | `FeedBack/Feedback.html` | Saved feedback submissions |

Because all modules share the same `localStorage`, future development should use namespaced keys to avoid collisions. Example:

```text
mindmirror.routine.daily.2026-05-30
mindmirror.routine.customTasks
mindmirror.water.history
mindmirror.feedback.entries
```

## 9. Important Implementation Notes

- The project is static and can be opened directly in a browser.
- No server is required.
- No install step is required.
- Data is browser-specific and device-specific.
- Clearing browser storage deletes app data.
- Running the same files from a different origin or path can create a different storage scope depending on browser behavior.
- Excel export depends on the external SheetJS CDN script.
- Several files contain mojibake/encoding artifacts where emoji or non-ASCII text appears corrupted. This is probably an encoding mismatch from earlier edits or file saves.
- `Routine/Routine.html` references a missing `pushup.js` script.
- Much of the module CSS and JavaScript is inline. This makes each module easy to open alone, but harder to share, test, and maintain.
- Inline `onclick` handlers couple HTML directly to global JavaScript functions.

## 10. How a New Developer Should Understand the Project

Start with `index.html`. It explains the product composition: a dashboard page that embeds or links to independent wellness tools.

Then inspect the modules in this order:

1. `Routine/Routine.html`
2. `Routine/Routine.js`
3. `Routine/HolidayTask.js`
4. `PushUp/pushup100.html`
5. `PushUp/MaintainPushUp365Day.html`
6. `Water/water8GlassesEveryDay.html`
7. `FeedBack/Feedback.html`

The most important concept is that this is not a single JavaScript application with shared state management. It is a collection of static pages that communicate only indirectly by sharing the same browser storage.

When changing behavior, identify:

- Which HTML file owns the screen
- Which script owns the event handler
- Which `localStorage` key stores the data
- Whether the page is embedded in an iframe
- Whether the code runs standalone or inside the dashboard

## 11. Local Development

The simplest way to run the project is to open `index.html` in a browser.

For more predictable iframe, CDN, and browser security behavior, a local static server is better. From the repository root, one option is:

```powershell
python -m http.server 8000
```

Then open:

```text
http://localhost:8000/
```

No compilation or dependency installation is required.

## 12. Suggested Future Improvements

- Fix file encoding so emoji and special characters render correctly.
- Remove or add the missing `Routine/pushup.js` reference.
- Namespace all `localStorage` keys.
- Move inline CSS and JavaScript into separate files per module.
- Replace inline `onclick` attributes with `addEventListener`.
- Add a shared storage helper to standardize JSON parsing and saving.
- Add a simple export/import feature for local data backup.
- Add validation for email and numeric inputs.
- Add a small static-server development command in documentation.
- Consider a lightweight app structure if modules continue to grow.
