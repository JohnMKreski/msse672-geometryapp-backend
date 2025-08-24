# Triangle Middleware Application (MSSE 672)

A Spring Boot application designed as a learning project for MSSE 672 - Component Based Software Development. 
This backend-only system demonstrates core enterprise development skills including REST APIs, 
authentication via sockets, database persistence using JDBC and Hibernate, and dependency injection with Spring.

---

## 🔧 Features

- **RESTful Geometry APIs**
    - `/triangle` → Determine triangle type
    - `/quad` → Manage quadrilateral records (JDBC + Hibernate options)

- **Authentication Layer**
    - REST-based login/logout (`/auth`) with token support
    - Raw socket-based token issuance, validation, and revocation (port configurable)

- **Spring Components**
    - Spring MVC, DI, Config
    - Spring Data JPA (Hibernate), JDBC Template
    - Spring Boot Testing with JUnit 5 + H2
    - Swagger UI via Springdoc

- **Profiles**
    - Default: Hibernate
    - `test`: H2 + Spring Boot testing

---

## 📁 Project Structure

```bash
src/main/java/org.msse672.geometryapp
├── auth
│   ├── config/               # Socket auth properties
│   ├── controller/           # REST auth controller
│   ├── core/                 # Auth interfaces + in-memory impl
│   └── socket/               # Raw socket server + filter + client
│       └── client/           # CLI client tool
├── config/                   # Spring configuration (CORS, filters, OpenAPI)
├── controller/               # Quad and Triangle controllers
├── dto/                      # Response wrapper
├── legacy/                   # Legacy service factory (educational only)
├── model/                    # Triangle and quadrilateral domain models
├── repository/               # JPA repository for quads
├── service/                  # QuadService interface + Hibernate, JDBC, and InMemory implementations
└── TriangleMiddlewareApplication.java
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+ (as set in `pom.xml`)
- Maven
- MySQL (for JDBC/Hibernate demos)


### Build & Run
```bash
# Build
mvn clean install
```
```bash
# Run
mvn spring-boot:run
```

### Swagger UI
Once running:
```
http://localhost:8080/swagger-ui/index.html
```

### Run Unit Tests
```bash
mvn test
```

> 💡 Includes full integration tests using H2 and Spring Boot test context

---

### MySQL Setup
```sql
CREATE DATABASE geometryapp;
```

> Update `application.properties` with your DB credentials.

---

## 🔐 Socket Authentication Server

The raw socket server listens on a configurable port and supports:
- Login (username + password)
- Token verification
- Token revocation (logout)

Example usage: see `DefaultSocketAuthClient` or test suite `AuthSocketServerTest`

---

## 📦 Build Details

This project uses the following key dependencies (see `pom.xml`):
- Spring Boot 3.4.4
- Hibernate ORM 6.x
- MySQL Connector / H2 (test)
- JUnit Jupiter 5
- Springdoc OpenAPI 2.6.0

---

## 🧪 Testing Philosophy

All tests are organized under `src/test/java`. Weekly test coverage includes:
- REST API and controller logic
- Service layer logic for geometry types
- Socket server logic for multi-client auth scenarios
- Filters enforcing token gating

JUnit + Spring Boot used for full integration validation.

---

## 📄 License

MIT License

---

## 📚 Notes
- This project is part of the MSSE672 coursework and reflects iterative refactoring and architectural enhancements aligned with each week's objectives.
- Socket auth config is managed via `AuthSocketProperties` (host, port, enabled flag)
- Multithreading is handled via `ExecutorService` in `AuthSocketServer`
- Tests use H2 in-memory DB for isolation
- Legacy components (`legacy/`) exist solely for XML factory demonstration and are not wired into the main app context.
- Socket authentication credentials and ports are externalized in `application.properties`.

## Documentation
See [Docs/docs.md](Docs/docs.md) for a week-by-week summary of features and deliverables.