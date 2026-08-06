[🇬🇧 English](README.md) | [🇦🇷 Español](README.es.md)

# Order Management System

A backend for managing products, categories, and purchase orders, with JWT authentication, role/permission-based authorization, and per-user data ownership. Built with Spring Boot 4 and Java 21.

## Features

- **Auth**: JWT access + refresh tokens hashed, stored in Redis (device-based, with reuse detection).
- **Authorization**: role-based (`USER` / `EMPLOYEE` / `ADMIN`) with fine-grained, permission-based access control (`@RequiresPermission`).
- **Ownership scoping**: customers can only read/modify their own orders; staff (with `ORDER_READ_ALL`) can access any order. Cross-user access returns `404`, not `403`, to avoid leaking existence of other users' resources.
- **Domain**: Users, Orders (with items, status, and status history), Products, Categories (self-referencing parent/child).
- **Full CRUD** on Orders, Products, and Categories, with role-appropriate permission boundaries (e.g. `READ` open to all, `CREATE`/`UPDATE` for staff, `DELETE` restricted to admins).

## Tech stack

Java 21 · Spring Boot 4 · PostgreSQL 16 · Redis 7 · Flyway · Docker Compose · JWT · MapStruct · Lombok

## Running locally

Everything is containerized.

```bash
git clone <repo-url>
cd <project-folder>
cp .env.example .env   # fill in DB/Redis/JWT secrets
docker compose up
```

This spins up Postgres, Redis, and the app (multi-stage build, non-root runtime user). Flyway runs migrations automatically on startup. Seed data (dev profile) creates three test accounts: admin, employee, and customer, used throughout the API test suite below.

## Testing the API

There's no JUnit/Mockito suite yet (a deliberate time tradeoff for this stage of the project). Instead, `requests.http` (IntelliJ HTTP Client format) covers Auth, Users, Orders (+ items + status transitions), Categories, and Products end-to-end, including negative paths: wrong password, missing token, nonexistent resource, invalid status transitions, and permission boundaries per role.

To run it: open `requests.http` in IntelliJ/WebStorm with the HTTP Client plugin, run the login requests first (they chain the resulting token into subsequent requests via `client.global.set(...)`), then run the rest in order.

## Known limitations

- `OrderStatusHistory` has no DTO/endpoint exposed yet (service method exists, unused).
- Exception messages currently echo raw IDs/emails in 404 responses — cleanup planned.

## Roadmap

- Trim exception messages.
- Folder structure cleanup.
- JUnit/Mockito test coverage.