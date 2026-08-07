![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

[🇬🇧 English](README.md) | [🇦🇷 Español](README.es.md)

# Order Management System

A backend for managing products, categories, and purchase orders, with JWT authentication, role and permission based authorization, and per user data ownership. Built with Spring Boot 4 and Java 21.

## Table of Contents

- [Features](#features)
- [API](#api)
- [Architecture & Design Decisions](#architecture--design-decisions)
- [Project Structure](#project-structure)
- [Security](#security)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Running Locally](#running-locally)
- [Testing the API](#testing-the-api)
- [Improvements Pending](#improvements-pending)
- [Roadmap](#roadmap)

## Features

- JWT Authentication (Access + Refresh Tokens)
- Refresh Token Rotation with Reuse Detection
- Hashed Refresh Tokens stored in Redis
- Role-Based Access Control (RBAC)
- Permission-Based Authorization
- Resource Owner Scoping
- Product, Category and Order Management
- Hierarchical Categories
- Order Status History
- Flyway Database Migrations
- Docker Compose Environment
- OpenAPI Documentation (SpringDoc + ReDoc)

## Why This Project

This project started as a way to go deeper into concepts used in real backend applications. The focus was on implementing solid authentication and authorization, organizing the codebase by domain, versioning the database properly, and building a reproducible environment through Docker Compose.

## Architecture

![Architecture diagram](docs/architecture.png)

## Architecture & Design Decisions

- Refresh tokens are hashed before being stored in Redis, never stored in plain text.
- Cross user access to resources returns 404, not 403, to avoid confirming the existence of resources that belong to someone else.
- Authorization is permission based (`@RequiresPermission`), not just role based, so access rules can be adjusted without touching business logic.
- Flyway handles all schema changes, so the database state is versioned and reproducible.
- The codebase is organized by domain (feature based packaging) rather than by technical layer, so everything related to one concept (`order`, `product`, `category`, `user`) lives together.

## Security

- JWT access and refresh tokens
- Refresh token rotation, with reuse detection
- Refresh tokens hashed and stored in Redis (device based)
- Owner scoping on order access and modification
- Role and permission based authorization

## Tech Stack

| Category      | Technology                 |
|----------------|------------------------------|
| Language       | Java 21                     |
| Framework      | Spring Boot 4               |
| Database       | PostgreSQL                  |
| Cache          | Redis                       |
| Migrations     | Flyway                       |
| Containers     | Docker Compose                |
| Security       | JWT                           |
| Mapping        | MapStruct                     |
| Documentation  | SpringDoc OpenAPI + ReDoc       |

## API

The API is documented with SpringDoc OpenAPI and published via ReDoc on GitHub Pages.

📖 **Documentation:** https://santigalarza.github.io/order-management-system/

### Resources

- `/auth`
- `/users`
- `/orders`
- `/products`
- `/categories`

## Project Structure

```
src/main/java
└── com.santiGalarza.ordermanagement
    ├── security
    ├── user
    ├── order
    ├── product
    ├── category
    ├── common
    └── config
```

## Requirements

- Java 21
- Docker
- Docker Compose

## Running Locally

The application is fully containerized.

```bash
git clone <repo-url>
cd <project-folder>
cp .env.example .env   # fill in DB/Redis/JWT secrets
docker compose up
```

This spins up Postgres, Redis, and the app (multi-stage build, non-root runtime user). Flyway runs migrations automatically on startup. Seed data (dev profile) creates three test accounts: admin, employee, and customer, used throughout the API test suite below.

## Testing the API

There is no JUnit/Mockito suite yet. Instead, `requests.http` (IntelliJ HTTP Client format) covers Auth, Users, Orders (with items and status transitions), Categories, and Products end to end, including negative paths: wrong password, missing token, nonexistent resource, invalid status transitions, and permission boundaries per role.

To run it: open `requests.http` in IntelliJ/WebStorm with the HTTP Client plugin, run the login requests first (they chain the resulting token into subsequent requests via `client.global.set(...)`), then run the rest in order.

## Improvements Pending

- Standardize error responses across all endpoints.
- Expose order status history through a dedicated endpoint.
- Define read permission granularity between customer and employee on order items.

## Roadmap

- [x] JWT Authentication
- [x] Refresh Token Rotation
- [x] Redis
- [x] Docker Compose
- [x] Flyway
- [x] API documentation with SpringDoc OpenAPI and ReDoc
- [ ] Unit tests (JUnit + Mockito)
- [ ] CI with GitHub Actions
- [ ] Observability with Spring Boot Actuator