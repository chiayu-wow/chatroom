# Chatroom API

A real-time chat application backend built with Spring Boot 3.x, WebSocket (STOMP), and JWT authentication.

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

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                        Client                           │
│              (Browser / Mobile App)                     │
└────────────┬──────────────────────┬────────────────────┘
             │ REST (HTTP)          │ WebSocket (STOMP)
             ▼                      ▼
┌────────────────────────────────────────────────────────┐
│                    Spring Boot App                     │
│                                                        │
│  ┌─────────────┐        ┌──────────────────────────┐  │
│  │ JWT Filter  │        │   WebSocket Handler      │  │
│  └──────┬──────┘        │  /app/chat/{roomId}      │  │
│         │               │  /topic/room/{roomId}    │  │
│  ┌──────▼──────┐        └────────────┬─────────────┘  │
│  │ Controllers │                     │ @Async          │
│  │  /api/auth  │                     ▼                 │
│  │  /api/rooms │             ┌───────────────┐         │
│  └──────┬──────┘             │    Service    │         │
│         └──────────┬─────────┘               │         │
│                    ▼                          │         │
│             ┌────────────┐                   │         │
│             │ Repository │◄──────────────────┘         │
│             └─────┬──────┘                             │
└───────────────────┼────────────────────────────────────┘
                    │
                    ▼
            ┌──────────────┐
            │  PostgreSQL  │
            └──────────────┘
```

---

## Data Model

```
User
├── id (UUID)
├── username (unique)
├── email (unique)
├── password (bcrypt)
└── createdAt

Room
├── id (UUID)
├── name
├── description
├── createdBy → User
└── createdAt

Message
├── id (UUID)
├── content
├── sender → User
├── room → Room
└── createdAt
```

---

## API Endpoints

### Auth
| Method | Path | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | 註冊新使用者 | — |
| POST | `/api/auth/login` | 登入，回傳 JWT | — |

### Rooms
| Method | Path | Description | Auth |
|---|---|---|---|
| GET | `/api/rooms` | 取得所有公開房間 | JWT |
| POST | `/api/rooms` | 建立新房間 | JWT |
| GET | `/api/rooms/{id}/messages` | 取得歷史訊息 | JWT |

### WebSocket
| Type | Destination | Description |
|---|---|---|
| Subscribe | `/topic/room/{roomId}` | 接收房間即時訊息 |
| Send | `/app/chat/{roomId}` | 發送訊息到房間 |

> WebSocket 連線端點：`ws://localhost:8080/ws`
> JWT 在 WebSocket handshake 時驗證（query param 或 header）

---

## Project Structure

```
src/main/java/com/example/chatroom/
├── ChatroomApplication.java     # 進入點
├── controller/                  # REST 端點
├── websocket/                   # STOMP handler
├── service/                     # 業務邏輯
├── repository/                  # JPA Repository
├── model/                       # Entity (User, Room, Message)
├── dto/                         # Request / Response DTO
├── security/                    # JWT filter & UserDetails
├── config/                      # Security, WebSocket, Async 設定
└── exception/                   # GlobalExceptionHandler
```

---

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose

### Run with Docker Compose

```bash
docker-compose up -d
```

### Run Locally

```bash
# 設定環境變數
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/chatroom
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=your-256-bit-secret

mvn spring-boot:run
```

### API Documentation

啟動後開啟 Swagger UI：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL 連線 URL | `jdbc:postgresql://localhost:5432/chatroom` |
| `SPRING_DATASOURCE_USERNAME` | DB 使用者 | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB 密碼 | `postgres` |
| `JWT_SECRET` | JWT 簽名金鑰（建議 256 bits+） | *(須設定)* |

---

## Monitoring

| Endpoint | Description |
|---|---|
| `/actuator/health` | 服務健康狀態 |
| `/actuator/metrics` | 應用程式指標 |
| `/actuator/prometheus` | Prometheus 格式指標 |

Custom metrics:
- `chat.messages.sent` — 發送訊息總數
- `chat.rooms.active` — 活躍房間數
