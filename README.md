# Smart Stock Backend

A Spring Boot backend for inventory and stock management with JWT-based authentication, role-based access control, products, categories, and inventory transaction management.

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Run Locally](#run-locally)
  - [Run with PostgreSQL](#run-with-postgresql)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Testing](#testing)
- [Configuration](#configuration)
- [Notes](#notes)

## Project Overview

`smart-stock-backend` is a Java Spring Boot backend application designed to manage products, categories, and inventory flows. It includes secure authentication using JWT tokens and role-based access control for normal users and admins.

## Key Features

- JWT-based login and authentication
- Role-based access: `USER` and `ADMIN`
- Category CRUD and lookup endpoints
- Product CRUD and list/search endpoints
- Inventory transactions for add/remove/correct stock
- Audit-friendly inventory history
- H2 in-memory database support for local development
- Optional PostgreSQL profile support
- OpenAPI / Swagger UI support for interactive API exploration
- Unit tests for service and application flows

## Technology Stack

- Java 21
- Spring Boot 3.3
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway
- H2 Database (default runtime)
- PostgreSQL driver
- JSON Web Tokens (JJWT)
- Springdoc OpenAPI
- JUnit 5, Mockito

## Architecture

The backend is organized into modular packages:

- `category` - category domain, repository, service, controller
- `product` - product domain, repository, service, controller
- `inventory` - inventory transactions and business logic
- `user` - authentication, registration, user details, roles
- `security` - JWT utils, authentication filter, Spring Security config
- `api` - global exception handling and API error responses

## Getting Started

### Prerequisites

- Java 21
- Maven
- (Optional) PostgreSQL if you want to run the PostgreSQL profile

### Run Locally

The project is configured to start with an in-memory H2 database by default.

```powershell
cd c:\Users\Nico\Desktop\JavaProject\smart-stock-backend
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.10'
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
.\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
```

The application starts on `http://localhost:8080`.

### Run with PostgreSQL

If you want to use PostgreSQL instead of H2, activate the `postgres` profile and configure your database properties in `application-postgres.yml` or environment variables.

```powershell
.\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

## API Endpoints

### Public endpoints

- `POST /api/auth/register` - register a new user
- `POST /api/auth/login` - authenticate and receive JWT token

### Category endpoints

- `POST /api/admin/categories` - create a new category (`ADMIN` only)
- `GET /api/categories` - list all categories
- `GET /api/categories/{id}` - get category by ID

### Product endpoints

- `POST /api/admin/products` - create a product (`ADMIN` only)
- `PUT /api/admin/products/{id}` - update a product (`ADMIN` only)
- `DELETE /api/admin/products/{id}` - delete a product (`ADMIN` only)
- `GET /api/products` - search/list products
- `GET /api/products/{id}` - get product by ID

### Inventory endpoints

- `PATCH /api/admin/inventory/{productId}/add` - add stock (`ADMIN` only)
- `PATCH /api/admin/inventory/{productId}/remove` - remove stock (`ADMIN` only)
- `PATCH /api/admin/inventory/{productId}/correct` - correct stock (`ADMIN` only)
- `GET /api/inventory/history/{productId}` - get inventory history for a product

## Authentication

The application uses JWT tokens for authentication.

1. Register a user via `POST /api/auth/register`
2. Log in via `POST /api/auth/login`
3. Copy the returned token and send it in the `Authorization` header:

```http
Authorization: Bearer <your-token>
```

### Roles

- `ROLE_USER` can access `GET /api/**` endpoints
- `ROLE_ADMIN` can access all protected endpoints, including `/api/admin/**`

## Testing

Run the test suite with Maven:

```powershell
.\tools\apache-maven-3.9.9\bin\mvn.cmd test
```

The project currently includes unit tests for authentication and inventory services.

## Configuration

The default configuration is stored in `src/main/resources/application.yml`:

- H2 in-memory datasource
- JPA `hibernate.ddl-auto=update`
- Flyway enabled
- JWT secret and expiration

The JWT settings are configured as:

```yaml
jwt:
  secret: change-this-secret-to-a-long-random-value-please-change-in-production
  expiration-ms: 86400000
```

> Important: Replace the default secret before using the project in production.

## Notes

- The default database is H2 for fast local development.
- The project ships with Flyway enabled, but there are no migrations yet.
- Swagger UI is available via Springdoc at `/swagger-ui/index.html` and OpenAPI docs at `/v3/api-docs`.
- The application is ready for extension with more business rules, refresh token support, and production-ready database migrations.

---

If you want, I can also add a `docker-compose.yml` and a PostgreSQL-ready setup section next.