---
# Kiến trúc Spring Boot Library

## Mục đích

Dùng tài liệu này khi cần định hướng trước khi sửa code. Project dùng Spring Boot 3.4.3, Java 17; entry point là `com.springboot.prj.LibApplication`. `@ComponentScan("com.springboot")` bao phủ toàn bộ boundary của ứng dụng, vì vậy bean mới nên nằm dưới package này.

## Cấu trúc package

- `com.springboot.lib`: DTO dùng chung, helper, exception, AOP logging, cache, Redis, queue và state machine.
- `com.springboot.jpa`: entity và Spring Data repository.
- `com.springboot.prj`: feature mẫu user/country/test và EventBus handler.
- `com.springboot.cdn`: metadata CDN, upload và serve file.
- `com.springboot.notification`: điều phối notification và tích hợp các channel.
- `com.springboot.ws`: WebSocket connector, message, session và room.

## Style phải giữ

- Dùng constructor injection với dependency `private final`.
- Controller mỏng: nhận request, check null cơ bản, gọi service, trả `ControllerHelper.success(...)`.
- REST trả `ResponseEntity<?>` và response envelope hiện có.
- Mapper thủ công với `toEntity`/`toDTO`; không để lộ password hoặc field nội bộ trong DTO.
- Giữ pattern package `features`, `service`, `request`, `response`/`dto`, `cache`, `route`.
- Phân biệt code thư viện với code demo; `UserServiceImpl` có đoạn minh họa EntityManager/cache không nên copy nguyên xi.

## Quy trình

1. Tìm class trực tiếp quyết định behavior.
2. Trace `controller -> service -> mapper/repository/helper`.
3. Giữ `ControllerHelper`, `ResponseData`, `AppException`, error code module, `EStatus` và localization.
4. Viết test tập trung dưới `src/test/java`.
5. Chạy `./mvnw.cmd test` từ root.

Kafka hiện chỉ là dependency/stub; queue đang hoạt động là EventBus synchronous. Không dùng `target/` làm source of truth.
