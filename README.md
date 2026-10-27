# Adil Constructions — Backend API

Spring Boot REST API for construction project management with JWT auth, PostgreSQL, and email notifications.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Email | Spring Mail (Gmail SMTP) |
| Build | Maven |
| Util | Lombok |

---

## Prerequisites

Make sure the following are installed and running before starting:

- **Java 17+** — `java -version`
- **Maven 3.6+** — `mvn -version`
- **PostgreSQL** — running on `localhost:5432`

---

## Database Setup

```sql
CREATE DATABASE adil_construction;
```

Default local connection (from `application.properties`):

| Property | Default Value |
|---|---|
| URL | `jdbc:postgresql://localhost:5432/adil_construction` |
| Username | `kaifkhan` |
| Password | *(empty)* |

If your Postgres credentials differ, set environment variables:

```bash
export SPRING_DATASOURCE_USERNAME=your_pg_user
export SPRING_DATASOURCE_PASSWORD=your_pg_password
```

---

## Running Locally

```bash
cd constructerApp
mvn spring-boot:run
```

App starts on **http://localhost:8080**

> If port 8080 is already in use:
> ```bash
> lsof -ti:8080 | xargs kill -9
> ```

---

## Default Admin Credentials

Auto-seeded on first startup:

| Field | Value |
|---|---|
| Email | `admin@constructer.com` |
| Password | `Admin@1234` |

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8080` | Server port |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/adil_construction` | DB URL |
| `SPRING_DATASOURCE_USERNAME` | `kaifkhan` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)* | DB password |
| `JWT_SECRET` | *(dev default)* | JWT signing key |
| `JWT_EXPIRATION` | `86400000` | Token TTL (ms) |
| `SPRING_MAIL_USERNAME` | `kkaif2687@gmail.com` | Gmail sender |
| `SPRING_MAIL_PASSWORD` | *(app password)* | Gmail app password |
| `ADMIN_BOOTSTRAP_EMAIL` | `admin@constructer.com` | Seed admin email |
| `ADMIN_BOOTSTRAP_PASSWORD` | `Admin@1234` | Seed admin password |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:5174` | Allowed frontend origins |

---

## Build JAR

```bash
mvn clean package -DskipTests
java -jar target/constructerApp-0.0.1-SNAPSHOT.jar
```

---

## Run with Docker

```bash
docker build -t constructer-api .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/adil_construction \
  -e SPRING_DATASOURCE_USERNAME=kaifkhan \
  -e SPRING_DATASOURCE_PASSWORD= \
  constructer-api
```

---

## Key Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Login, returns JWT |
| GET | `/api/projects` | Admin | List all projects |
| POST | `/api/projects` | Admin | Create project |
| GET | `/api/payments` | Admin | List payments |
| POST | `/api/leads` | Public | Submit a lead |
| GET | `/api/clients` | Admin | List clients |

Pass JWT as: `Authorization: Bearer <token>`

---

## Project Structure

```
src/main/java/com/AdilProject/constructerApp/
├── config/          # Security, CORS, JWT, DataSeeder
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities
├── repository/      # Spring Data repositories
└── service/         # Business logic
```
