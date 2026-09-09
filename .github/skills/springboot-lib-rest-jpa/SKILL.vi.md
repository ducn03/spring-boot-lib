---
# Workflow REST và JPA

## Convention response

Controller dùng `@RestController`, constructor injection, `ResponseEntity<?>`, route constant và trả `ControllerHelper.success(...)` hoặc `ControllerHelper.error(...)`. `ResponseData` chứa `Meta` và `data`; paging nằm trong `PagingData`, được tạo từ request và cập nhật bằng `PagingData.update(Page)`.

Dùng `@LogActivity` khi endpoint cần set locale, log request/response async, lưu `HttpLog` và chuyển `AppException`/unexpected exception thành response. Không có `@ControllerAdvice` toàn cục nên endpoint không có AOP sẽ có error behavior khác.

## Layering

1. Request đặt trong `service.<feature>.request`, output đặt trong `response` hoặc `dto` theo feature bên cạnh.
2. Controller chỉ check null/request absence cơ bản; business validation ở service hoặc validator.
3. Service trả DTO/list, không trả entity trực tiếp.
4. Mapper component map thủ công `toEntity` và `toDTO`.
5. Query đơn giản nằm ở repository; filter động dùng JPA Specification.
6. Resource không tồn tại phải ném `AppException` với error code đúng module.

## JPA

Repository scan từ `com.springboot.jpa.repository`, entity scan từ `com.springboot.jpa.domain`. `BaseEntity` cung cấp id, optimistic `@Version`, integer `status`, created/updated auditing. Dùng `EStatus.ACTIVE` khi query active và `EStatus.DELETED` cho soft delete. Datasource dùng namespace `spring.datasource.springboot`, transaction manager là `springbootTransactionManager`.

Nhớ rằng `@Transactional` chạy qua proxy; self-invocation như `UserServiceImpl.testEM` không tạo transaction mới.

## Kiểm tra

Khi sửa endpoint phải kiểm tra request binding, mapper fields, repository filter, status, response envelope, error code và message localization cùng nhau. Chạy `./mvnw.cmd test`; integration test cần MySQL/Redis.
