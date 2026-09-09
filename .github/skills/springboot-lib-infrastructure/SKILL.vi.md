---
# Workflow Infrastructure và Cache

`application.yaml` bật profile `dev`, server mặc định `localhost:9000`. `application-dev.yml` mặc định MySQL `localhost:3307`, Redis `localhost:6379`, database `5`.

`DatabaseConfiguration` bind datasource ở `spring.datasource.springboot.jdbc-url`, username, password, driver và Hikari; chỉ cấu hình `spring.datasource.url` là chưa đủ. Hibernate dùng `ddl-auto: update`, cần kiểm tra entity trước khi tin vào SQL init.

`RedisConfig` tự tạo Lettuce factory, `RedisTemplate<String, String>`, listener container và Lua bean `main-single-request`. `RedisService` xử lý value/hash/increment/TTL/duplicate request. Kiểm tra `RedisConstant` trước khi tạo key; một số hash helper giả định value tồn tại.

`CacheService` là in-memory bounded TTL/LRU cache có cleanup/statistics. `LazyCache` reload theo thời gian hết hạn. Khi sửa phải giữ thread-safety, expiry và invalidation.

Compose hiện dùng biến datasource/Redis chuẩn, profile `prod`, port `8080`, trong khi Java code cần namespace datasource riêng, profile `dev`, port `9000`; phải reconcile trước khi chạy app bằng compose.

Kiểm tra property wiring, cache hit/miss/expiry/eviction và duplicate-request TTL. Phân biệt test compile với kiểm chứng connection thật.
