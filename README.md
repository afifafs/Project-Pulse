# Project Pulse

Project Pulse is now organized around two app folders:

- `backend/` contains the Spring Boot API and seeded H2 demo data
- `frontend/` contains the Vue + Vite demo dashboard

For local demo startup:

1. Start the backend from `backend/` with `mvn spring-boot:run`
2. Start the frontend from `frontend/` with `npm run dev`
3. Open `http://localhost:5173`

The frontend proxies API requests to the backend, so both apps run together locally without extra CORS setup.

Notes:

- The backend seeds sample sections, teams, students, instructors, and rubrics on startup.
- There are a few legacy top-level files and build artifacts in the repo root from earlier merges; they are not part of the current demo flow.
