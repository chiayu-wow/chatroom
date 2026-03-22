# Chat Room API — Spring Boot 3.x Project

## 技術棧
- Java 17, Spring Boot 3.x, Maven
- Spring Security + JWT（jjwt 0.12.x）
- Spring WebSocket + STOMP
- Spring Data JPA + PostgreSQL
- Springdoc OpenAPI 3（Swagger UI）
- Spring Actuator + Micrometer + Prometheus
- Logback + MDC（trace ID）
- JUnit 5 + Mockito + Testcontainers
- Docker + Docker Compose
- GitHub Actions CI/CD

## 常用指令
- `mvn spring-boot:run` — 本地啟動
- `mvn test` — 執行測試
- `docker-compose up -d` — 啟動全部服務

## 專案結構
src/main/java/com/example/chatroom/
├── controller/      # REST 端點（房間管理）
├── websocket/       # WebSocket handler, STOMP 設定
├── service/         # 業務邏輯
├── repository/      # JPA Repository
├── model/           # Entity
├── dto/             # Request / Response DTO
├── security/        # JWT filter
├── config/          # Security, WebSocket, Async 設定
└── exception/       # GlobalExceptionHandler

## Entity 設計
- User：id, username, email, password, createdAt
- Room：id, name, description, createdBy, createdAt
- Message：id, content, sender(User), room(Room), createdAt

## WebSocket 規範
- 連線端點：ws://localhost:8080/ws
- 訂閱頻道：/topic/room/{roomId}
- 發送訊息：/app/chat/{roomId}
- JWT 驗證在 WebSocket handshake 時進行

## API 設計
REST（需 JWT）：
- POST /api/auth/register
- POST /api/auth/login
- GET  /api/rooms — 取得所有公開房間
- POST /api/rooms — 建立房間
- GET  /api/rooms/{id}/messages — 取得歷史訊息

WebSocket（需 JWT）：
- 加入房間後訂閱 /topic/room/{roomId}
- 發訊息到 /app/chat/{roomId}

## 併發規範
- 多人同時發訊息用 @Async 非同步存入 DB
- WebSocket session 管理注意 thread safety
- 訊息廣播由 SimpMessagingTemplate 處理

## 監控與日誌
- 每條 WebSocket 訊息記錄 sender + roomId + timestamp
- 自訂 metrics：chat.messages.sent, chat.rooms.active
- Actuator 暴露：health, metrics, prometheus

## 環境變數
| 變數名稱 | 說明 |
|---|---|
| SPRING_DATASOURCE_URL | PostgreSQL 連線 URL |
| SPRING_DATASOURCE_USERNAME | DB 使用者名稱 |
| SPRING_DATASOURCE_PASSWORD | DB 密碼 |
| JWT_SECRET | JWT 簽名金鑰 |
