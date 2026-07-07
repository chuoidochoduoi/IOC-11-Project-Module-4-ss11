# Redis Pub/Sub - Message Broker nhẹ nhàng

## 📡 Kiến trúc

```
┌─────────────────┐                    ┌─────────────────┐
│   PUBLISHER     │                    │   SUBSCRIBER      │
│ (AlertController) │────── Redis ──────▶│(RedisMessage-    │
│                 │    Channel         │ Subscriber)       │
└────────┬────────┘    "pharmacy-alerts" └───────▲─────────┘
         │                                       │
         │                                       │
         │    (Fire-and-forget - không lưu trữ)   │
         │    Message đến ngay lập tức             │
         │                                       │
         ▼                                       │
┌─────────────────┐                              │
│ Console Log Output"                             │
│ (in ra message) ◀──────────────────────────────┘
└─────────────────┘
```

## 📦 Các thành phần

| File | Vai trò |
|------|--------|
| `model/AlertMessage.java` | Message DTO (type, message, timestamp) |
| `service/RedisMessagePublisher.java` | Gửi message đến channel |
| `service/RedisMessageSubscriber.java` | Lắng nghe và xử lý message |
| `config/RedisConfig.java` | Cấu hình đăng ký subscriber |
| `controller/v1/AlertController.java` | API `/api/v1/alert` |

## 🧪 Test

### 1. Khởi động Redis
```powershell
docker run -d --name redis-cache -p 6379:6379 redis:latest
```

### 2. Chạy ứng dụng
```powershell
.\gradlew bootRun
```

### 3. Gửi alert nhập hàng
```powershell
# Gửi alert
curl -X POST "http://localhost:8080/api/v1/alert/import?message=Đã nhập 100 hộp Panadol"

# Hoặc POST JSON
curl -X POST http://localhost:8080/api/v1/alert `
  -H "Content-Type: application/json" `
  -d '{"type":"IMPORT","message":"Đã nhập 100 hộp Panadol"}'
```

### 4. Console sẽ hiển thị
```
========================================
>>> NHẬN ALERT TỪ REDIS CHANNEL <<<
Type: IMPORT
Message: Đã nhập 100 hộp Panadol
Time: 1719999999999
========================================
```

### 5. Kiểm tra channel trên Redis
```powershell
docker exec -it redis-cache redis-cli
> PUBLISH pharmacy-alerts '{"type":"TEST","message":"Test channel"}'
> KEYS *  # Channel không lưu key vì là pub/sub
```

## 📝 Lưu ý

- Redis Pub/Sub là **fire-and-forget**: message không được lưu trữ
- Nếu offline khi có message, sẽ **KHÔNG** nhận được
- Dùng cho thông báo tức thời (notification, alert)
- Để lưu trữ message cần dùng **Redis Streams** hoặc **Queue**