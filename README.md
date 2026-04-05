# Finance Data Processing and Access Control Backend

A backend system for a finance dashboard with JWT authentication and role-based access control.

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- PostgreSQL
- Spring Security + JWT
- Spring Data JPA
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

The app starts on port 8080. On startup, three roles are automatically created in the database: ADMIN, ANALYST, and VIEWER. No manual setup needed.

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Setup Notes

To create users with different roles, use the register endpoint with `roleName` set to `ADMIN`, `ANALYST`, or `VIEWER`. The first user you create should be ADMIN so you can test all endpoints.

## API Explanation

### Authentication

These endpoints are public and do not require a token.

`POST /api/v1/auth/register` — Creates a new user. Requires name, email, password, and roleName in the request body. Returns a JWT token on success.

`POST /api/v1/auth/login` — Logs in an existing user. Returns a JWT token which must be used in all subsequent requests as `Authorization: Bearer <token>`.

### Financial Records

All endpoints below require a valid JWT token in the Authorization header.

`POST /api/v1/records` — Creates a new financial record. Requires amount, type (income or expense), category, and date. Only ADMIN and ANALYST can access this.

`GET /api/v1/records` — Returns all non-deleted records with pagination. Accepts `page` and `size` as query params. Example: `/api/v1/records?page=0&size=10`

`GET /api/v1/records/{id}` — Returns a single record by ID.

`PUT /api/v1/records/{id}` — Updates an existing record. Only ADMIN and ANALYST can access this.

`DELETE /api/v1/records/{id}` — Soft deletes a record. The record is not removed from the database, only marked as deleted. Only ADMIN can access this.

`GET /api/v1/records/filter` — Filters records by type, category, or date range. Example: `/api/v1/records/filter?type=income` or `/api/v1/records/filter?startDate=2026-04-01&endDate=2026-04-30`

`GET /api/v1/records/search` — Searches records by keyword across category and notes fields. Example: `/api/v1/records/search?keyword=salary`

### Dashboard

`GET /api/v1/dashboard/summary` — Returns total income, total expense, net balance, and a SURPLUS or DEFICIT status.

`GET /api/v1/dashboard/category-wise` — Returns total amount grouped by category across all records.

`GET /api/v1/dashboard/recent` — Returns the 5 most recent transactions.

`GET /api/v1/dashboard/monthly-trends` — Returns income and expense totals grouped by month and year. Only ADMIN and ANALYST can access this.

## Roles and Access

ADMIN has full access to all endpoints including delete and monthly trends.

ANALYST can create and update records and view all dashboard data but cannot delete records.

VIEWER has read-only access. Can view records, filter, search, and see dashboard summary but cannot create, update, or delete anything.

## Assumptions

- Each user is assigned exactly one role at the time of registration and it does not change
- Deleted records are soft deleted meaning they remain in the database with an is_deleted flag set to true, so the data is never lost
- JWT tokens expire after 24 hours
- The type field in records only accepts income or expense, validated at the API level
- All monetary values are stored as BigDecimal to avoid floating point precision issues common in financial calculations

## Tradeoffs

- Soft delete was chosen over hard delete to preserve audit history, which is important in financial systems. The tradeoff is that the database grows over time and queries always need to filter out deleted records.
- JWT was used instead of sessions to keep the backend stateless and easier to scale. The tradeoff is that tokens cannot be invalidated before expiry unless a blocklist is maintained, which is not implemented here.
- Roles are kept simple with three fixed levels. A more flexible permission system could be built but would add significant complexity for the scope of this project.
- In-memory role seeding on startup keeps setup simple but means roles are tightly coupled to application code rather than being fully data-driven.