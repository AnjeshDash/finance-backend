# Finance Data Processing and Access Control Backend

A backend system for a finance dashboard with JWT authentication and role-based access control.

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- PostgreSQL
- Spring Security + JWT
- Maven

## Prerequisites

- Java 21
- Maven
- PostgreSQL

## Getting Started

Clone the repository
```bash
git clone https://github.com/AnjeshDash/finance-backend.git
cd finance-backend
```

Create a PostgreSQL database
```sql
CREATE DATABASE finance_db;
```

Update `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=your_password
app.jwt.secret=FinanceBackendSuperSecretKeyForJWTAtLeast256BitsLongString
app.jwt.expiration=86400000
```

Run the application
```bash
mvn spring-boot:run
```

The app starts on port 8080. Roles (ADMIN, ANALYST, VIEWER) are seeded automatically on startup.

Swagger UI is available at `http://localhost:8080/swagger-ui.html`

## API Overview

Authentication endpoints do not require a token. All other endpoints require `Authorization: Bearer <token>` in the header.

**Auth**
- POST /api/v1/auth/register
- POST /api/v1/auth/login

**Records**
- POST /api/v1/records
- GET /api/v1/records
- GET /api/v1/records/{id}
- PUT /api/v1/records/{id}
- DELETE /api/v1/records/{id}
- GET /api/v1/records/filter
- GET /api/v1/records/search

**Dashboard**
- GET /api/v1/dashboard/summary
- GET /api/v1/dashboard/category-wise
- GET /api/v1/dashboard/recent
- GET /api/v1/dashboard/monthly-trends

## Roles

ADMIN can do everything. ANALYST can read and create records but cannot delete. VIEWER has read-only access.

## Assumptions

- Each user has one role
- Deleted records are soft deleted, not permanently removed
- JWT token is valid for 24 hours