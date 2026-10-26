# Job Portal with Role-Based Access

A full-stack job portal with three roles (Candidate, Recruiter, Admin), JWT authentication, resume upload and application tracking.

## Tech stack
Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, MySQL 8, React 18 (Vite), Swagger/OpenAPI, JUnit 5, Mockito, Docker.

## Features
- Register and log in as a Candidate or Recruiter; a seeded Admin account manages users.
- JWT authentication with role-based access control on every endpoint.
- Recruiters post and delete jobs, then review applications and move each one through Applied, Shortlisted or Rejected.
- Candidates search jobs by title, company and location with pagination, apply with a PDF or Word resume, and track their status.
- Email notification on every status change (logged to the console unless SMTP is configured).
- Admin dashboard with platform statistics and user management.
- REST API documented with Swagger UI; service-layer logic covered by JUnit and Mockito tests.

## Run it
1. Start MySQL: `docker compose up -d mysql`
2. Backend: `cd backend && mvn spring-boot:run` (API on http://localhost:8080, Swagger at /swagger-ui.html)
3. Frontend: `cd frontend && npm install && npm run dev` (app on http://localhost:5173)

Run everything except the frontend in Docker with `docker compose up --build`.
Run the tests with `cd backend && mvn test`.

Demo admin login: `admin@jobportal.com` / `Admin@123`

## Main API endpoints
| Method | Path | Who |
|---|---|---|
| POST | /api/auth/register, /api/auth/login | Public |
| GET | /api/jobs?q=&location=&page=&size= | Public |
| POST | /api/jobs | Recruiter |
| DELETE | /api/jobs/{id} | Recruiter (owner) or Admin |
| POST | /api/applications/{jobId} | Candidate |
| GET | /api/applications/mine | Candidate |
| GET | /api/applications/recruiter | Recruiter |
| PUT | /api/applications/{id}/status | Recruiter (owner) |
| GET | /api/admin/stats, /api/admin/users | Admin |
