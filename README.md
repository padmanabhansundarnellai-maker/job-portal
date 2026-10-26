# Job Portal

A full-stack job portal with three roles: candidate, recruiter and admin.

**Stack:** Spring Boot 3.2 (Java 17+), Spring Security with JWT, MySQL 8, React 18 with Vite.

## Features
- Register and log in as candidate or recruiter (JWT)
- Recruiters post and delete jobs, review applicants, download resumes, change statuses
- Candidates search jobs, apply with a PDF resume, track status
- Admin dashboard with totals and user management
- Swagger UI at `/swagger-ui/index.html`

## Run locally or in GitHub Codespaces

Use three terminals.

1. Database:
   `docker compose up -d mysql`
2. Backend (port 8080):
   `cd backend && mvn spring-boot:run`
3. Frontend (port 5173):
   `cd frontend && npm install && npm run dev -- --host`

Open the frontend at `http://localhost:5173`. In Codespaces, open the forwarded port from the Ports tab.

## Default admin
- Email: `admin@jobportal.com`
- Password: `Admin@123` (change this in `DataSeeder.java` before any public use)

## Notes
- The frontend proxies `/api` to the backend through `vite.config.js`, and removes the `Origin` header so Spring's CORS check doesn't reject Codespaces URLs.
- On Java 23+, `pom.xml` enables Lombok through `annotationProcessorPaths` and pins Lombok 1.18.42 and Byte Buddy 1.17.7.
