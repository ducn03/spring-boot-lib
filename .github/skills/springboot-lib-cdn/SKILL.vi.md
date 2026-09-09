---
# Workflow CDN

## Ownership và flow

CDN nằm trong `com.springboot.cdn`; metadata dùng `Cdn` và `CdnRepository`. Flow REST là `CdnController` cho metadata/list, `FileController` cho upload/serve, sau đó tới `CdnServiceImpl` hoặc `FileServiceImpl`, mapper và repository.

Giữ constructor injection, `@RestController`, `ResponseEntity<?>`, `ControllerHelper.success(...)`, route constant/base controller nếu feature đang dùng. Endpoint cần logging/error conversion thì thêm `@LogActivity`.

## Upload

`FileServiceImpl` đọc `cdn.base-path`, mặc định `app/cdn`, tạo thư mục `<env>/<domain>/<group>`, clean filename, giữ extension, tạo tên SHA-256 kết hợp UUID dài 32 ký tự, copy multipart stream và lưu link tương đối vào entity `Cdn`.

Khi sửa upload:

1. Validate file rỗng/thiếu, enum env/domain, group và status.
2. Dùng `Path`, absolute path và normalize; không nối path filesystem bằng string thiếu kiểm soát.
3. Giữ nhất quán target filesystem, link DB, route và DTO.
4. Quyết định rõ overwrite, duplicate, extension và tạo directory.
5. Dùng `AppErrorCodes` của CDN và ném `AppException`.

## Security serve file

Chỉ `normalize()` là chưa đủ. Resolve path theo absolute CDN base, normalize cả hai, kiểm tra path cuối vẫn nằm dưới base trước khi tạo `UrlResource`. Test `..`, absolute path, encoded separator, file thiếu và file không readable.

CDN phải giữ độc lập với notification vì dependency và failure semantics khác nhau.
