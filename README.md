# MindMirror

MindMirror is a habit-tracking website with a guest mode and an optional account mode. Guests use browser Local Storage and do not need a backend. Signed-in users use the Spring Boot services under `services/` through the API Gateway, with MySQL as the primary account data store.

## Current features

- Daily routines, custom tasks, holiday tasks, and affirmations
- Water intake tracking
- Push-up challenges and workout tools
- Feedback saved locally in the browser

## Repository layout

```text
.
├── index.html                 # GitHub Pages entry point
├── src/
│   ├── features/               # Pages and assets grouped by product feature
│   ├── shared/                 # Browser-side utilities shared by features
│   └── styles/                 # Shared page styles
└── services/                   # Reserved for future independently deployed APIs
```

Keep the root `index.html` and use relative links for frontend pages and assets. This lets the site work both from a local checkout and when published under a GitHub Pages project-site path.

Keep each page's HTML, CSS, and page-specific JavaScript together in its feature folder. Use descriptive kebab-case filenames, and split distinct responsibilities into small scripts such as `routine-history.js` and `affirmations.js`. Put code shared by multiple features in `src/shared/`.

## GitHub Pages

In **Settings → Pages → Build and deployment**, publish the repository root from the branch containing the site. The site has no build step. GitHub Pages serves only the static frontend; it cannot run Spring Boot applications.

## Account backend

The account flow is split into independently deployable services:

```text
services/
├── api-gateway/       # frontend entry point on :8084
├── auth-service/      # registration, login, tokens on :8081
├── user-service/      # profiles on :8082
└── data-service/      # application data on :8083
```

See [`services/README.md`](services/README.md) for startup and deployment configuration. The frontend displays the account-offline message and offers retry or guest continuation when the gateway or a domain service cannot be reached. Guest data is only removed after a successful migration and explicit confirmation.

## Author

Bhupesh Baghel
