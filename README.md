# Chatroom API

A real-time chat application built with Spring Boot 3.x, WebSocket (STOMP), and JWT authentication.

---

## Quick Start

### Option A — IntelliJ (recommended for development)

**Step 1: Start the database**
```bash
docker-compose up -d postgres
```

**Step 2: Set environment variables in IntelliJ**

Go to **Run → Edit Configurations → ChatroomApplication → Environment variables** and add:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/chatroom
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=change-me-in-production-must-be-at-least-256-bits-long
```

**Step 3: Start the app**

Click the ▶ button in the top-right corner of IntelliJ.

**Step 4: Open the chat UI**

Go to [http://localhost:8080](http://localhost:8080) in your browser.

> If you changed any code, go to **Build → Rebuild Project** before restarting.

---

### Option B — Docker (one command)

```bash
docker-compose up -d --build
```

Then open [http://localhost:8080](http://localhost:8080).

---

### Stop the app

**IntelliJ:** Click the ■ stop button in IntelliJ, then:
```bash
docker-compose down
```

**Docker:**
```bash
docker-compose down
```

---

## URLs

| URL | Description |
|---|---|
| [http://localhost:8080](http://localhost:8080) | Chat UI |
| [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | API Docs (Swagger) |
| [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Health check |
| [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus) | Prometheus metrics |

---

## API Endpoints

### Auth
| Method | Path | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | — |
| POST | `/api/auth/login` | Login, returns JWT token | — |

### Rooms
| Method | Path | Description | Auth |
|---|---|---|---|
| GET | `/api/rooms` | List all rooms | JWT |
| POST | `/api/rooms` | Create a new room | JWT |
| GET | `/api/rooms/{id}/messages` | Get message history | JWT |

### WebSocket
| Type | Destination | Description |
|---|---|---|
| Subscribe | `/topic/room/{roomId}` | Receive real-time messages |
| Send | `/app/chat/{roomId}` | Send a message to a room |

> WebSocket endpoint: `ws://localhost:8080/ws?token=<JWT>`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.x |
| Security | Spring Security + JWT (jjwt 0.12.x) |
| Real-time | Spring WebSocket + STOMP |
| Database | PostgreSQL + Spring Data JPA |
| API Docs | Springdoc OpenAPI 3 (Swagger UI) |
| Monitoring | Spring Actuator + Micrometer + Prometheus |
| Build | Maven |

---

## Environment Variables

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `JWT_SECRET` | JWT signing key (256 bits+) |

---

## Project Structure

```
src/main/java/com/example/chatroom/
├── ChatroomApplication.java
├── controller/        # REST endpoints
├── websocket/         # STOMP handler
├── service/           # Business logic
├── repository/        # JPA repositories
├── model/             # Entities (User, Room, Message)
├── dto/               # Request / Response DTOs
├── security/          # JWT filter & UserDetails
├── config/            # Security, WebSocket, Async config
└── exception/         # Global exception handler
```
