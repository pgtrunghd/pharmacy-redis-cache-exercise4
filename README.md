# Bài 8344 — Chống bán quá số lượng với Redisson Distributed Lock

Đề bài: https://portal.rikkei.edu.vn/learn/159/homework/8344

Phát triển tiếp bài 8343. Mỗi thuốc có tồn kho `stockQuantity` trong PostgreSQL. API bán một hộp lấy Redisson `RLock` có key `lock:medicine:{id}` trước khi đọc tồn kho và trừ số lượng. `tryLock(3, 5, TimeUnit.SECONDS)` chờ tối đa 3 giây, giữ lock 5 giây; khối `finally` trả lock. Giao dịch PostgreSQL hoàn tất trước khi trả lock, nên yêu cầu tiếp theo sẽ thấy tồn kho mới. Nếu hết hàng, API trả `409 Conflict`.

## Chạy

Cần JDK 21, PostgreSQL, Redis. Tạo database `pharmacy_cache_db` rồi chạy:

```bash
DB_PASSWORD='mat-khau-cua-ban' ./gradlew bootRun
```

Biến tùy chọn: `DB_USERNAME`, `DB_NAME`, `DB_PORT`, `REDIS_HOST`, `REDIS_PORT`, `SERVER_PORT`.

## Thử hai nhân viên mua hộp cuối cùng

```bash
curl -X POST http://localhost:8081/api/v1/medicines \
  -H 'Content-Type: application/json' \
  -d '{"name":"Thuốc hiếm","price":100000}'

curl -X POST http://localhost:8081/api/v1/medicines/1/imports \
  -H 'Content-Type: application/json' -d '{"quantity":1}'

curl -s -w ' %{http_code}\n' -X POST http://localhost:8081/api/v1/medicines/1/sell &
curl -s -w ' %{http_code}\n' -X POST http://localhost:8081/api/v1/medicines/1/sell &
wait
```

Kết quả: một yêu cầu trả `200` với `remainingQuantity: 0`; yêu cầu kia trả `409` vì hết hàng. Đọc tồn kho bằng `GET /api/v1/medicines/1` thấy `stockQuantity: 0`. Các API cache TTL và Redis Pub/Sub ở bài trước vẫn có mặt; nhập hàng và bán hàng xóa cache chi tiết thuốc khi thành công.

Build: `./gradlew clean test bootJar`.
