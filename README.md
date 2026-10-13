# MindMirror

MindMirror is a static habit-tracking website. Its current pages run in the browser and save progress in local storage.

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

## Future backend services

When backend work begins, add each independently deployable service under `services/`, for example:

```text
services/
└── feedback-service/
	├── pom.xml
	└── src/
		├── main/java/
		├── main/resources/
		└── test/java/
```

Create the Spring Boot project only when implementing that service. Deploy it separately from GitHub Pages, then configure the frontend to call its hosted API. Keep service-specific build files and configuration inside each service directory.

## Author

Bhupesh Baghel