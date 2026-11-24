# Relax Oasis — Hotel Room Booking (Backend API)

Spring Boot REST API for the Relax Oasis hotel booking system.

**Frontend repo:** https://github.com/Htet2-Aung/Final-Hotel-Room-Booking

## Tech Stack

- Java 17, Spring Boot 3.1 (Web, Spring Data JPA, Spring Security, Validation, Mail)
- JWT authentication (jjwt 0.11.5), roles: `ROLE_USER` / `ROLE_ADMIN`
- MySQL 8 (default, persistent) or in-memory H2 (zero-setup demo)
- Maven Wrapper included — no Maven installation required

## IMPORTANT: Building is not Running

`./mvnw compile` or `./mvnw package` only **build** the project (`BUILD SUCCESS`).
Nothing is listening yet. To actually **start** the API use:

    ./mvnw spring-boot:run        (Windows: .\mvnw.cmd spring-boot:run)

Keep the terminal open. The API is up when you see:

    Tomcat started on port 8181
    Started HotelRoomBookingSeverApplication in X seconds

Stop it with `Ctrl+C`.

## Quick Start A — zero setup (H2 in-memory)

Only a JDK 17+ is needed. No MySQL.

1. Open `src/main/resources/application.properties` and comment out the first line:

       # spring.profiles.active=mysql

2. Run:

       ./mvnw spring-boot:run

3. API at http://localhost:8181 — demo users and sample rooms are seeded automatically.

Note: H2 data resets on every restart. Email sending is disabled until mail
credentials are configured (see below).

## Quick Start B — MySQL (persistent)

1. Install and start MySQL 8.
2. Keep `spring.profiles.active=mysql` (default). The database `newbookingdb1`
   is created automatically on first run.
3. Default MySQL login is `root` / `1234`. To use your own password, set the
   `MYSQL_ROOT_PASSWORD` environment variable or edit
   `src/main/resources/application-mysql.properties` locally.
4. Run `./mvnw spring-boot:run`.

## Email (optional)

Booking confirmations and the contact form send real email via Gmail SMTP.
Set these environment variables (or edit `application-mysql.properties` locally):

- `MAIL_USERNAME` — a Gmail address
- `MAIL_APP_PASSWORD` — its 16-character App Password
  (create one at https://myaccount.google.com/apppasswords)

Never commit real credentials — the properties file uses placeholders on purpose.

## Demo Accounts (auto-seeded when the database is empty)

| Role  | Email             | Password |
|-------|-------------------|----------|
| Admin | `admin@gmail.com` | `5678`   |
| User  | `user@gmail.com`  | `1234`   |

Sample room types and rooms are seeded as well, so the UI is not empty on first login.

## Main API Endpoints

| Area         | Base path                  | Notes |
|--------------|----------------------------|-------|
| Auth         | `/api/user/**`             | register, login (returns JWT) |
| Rooms        | `/api/room/**`             | CRUD + list |
| Room Types   | `/api/roomtype/**`         | CRUD + list |
| Bookings     | `/api/booking/**`          | create / confirm / delete, auto-email on confirm |
| Payments     | `/api/payment/**`          | wallet deposit & payments |
| Activity Log | `/api/activitylog/**`      | per-user audit trail: list, create, update, delete |
| Uploads      | `/api/upload`, `/uploads/**` | room images |

Send the JWT as an `Authorization` request header after login.

## Project Layout

    src/main/java/com/hostmdy/hotelbooking
      config/       security (JWT) + web config
      domain/       JPA entities (User, Booking, Room, RoomType, Payment, ActivityLog, ...)
      repository/   Spring Data repositories
      resource/     REST controllers
      service/      business logic
      payload/      request / response DTOs
