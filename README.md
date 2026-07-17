# MindMirror - "Track Your Habits, Protect Your Mind"

MindMirror is a full-stack personal habit tracker. The responsive static frontend works locally first and connects to the Spring Boot API after sign-in, so routine, hydration, and workout progress can be securely persisted.
# Features

- User account registration and login
- Daily routine management
- Custom task tracking
- Holiday task planner
- Daily affirmations
- Water intake tracker
- 100-Day Push-up Challenge
- 365-Day Push-up Maintenance Tracker
- prison workout method
- Feedback submission
- Responsive interface for desktop and mobile
- Connected home dashboard with account totals and activity streak
- MySQL migrations managed by Flyway, with readable, feature-focused table names

## Run locally

1. Set a strong `JWT_SECRET` environment variable.
2. Start MySQL and the API with `docker compose up --build`.
3. Serve the project root with a static web server on port `8000` (or update the allowed CORS origins). Open `http://localhost:8000`.

The API runs on port `8081`; its readiness endpoint is `http://localhost:8081/actuator/health/readiness`.

# What You Can Do

- Create and manage daily routines
- Track completed tasks
- Monitor daily water intake
- Record push-up progress
- Read daily affirmations
- Submit feedback
- Keep your personal progress organized

# Screenshot

### Home
![Home](src\assets\home.png)

## Author

Bhupesh Baghel
