---
# Workflow Notification

## Ownership và flow

Notification nằm trong `com.springboot.notification`: `NotifyController` nhận route, `NotifyService` điều phối, `MessageSenderFactory` chọn sender, `NotifyBuilder` build content, còn `EmailSender`, `SmsSender`, `ZaloSender`, `AppSender` xử lý channel.

Giữ constructor injection, `@RestController`, `ResponseEntity<?>`, `ControllerHelper.success(...)`. Lỗi method/sender dùng `AppException` và `AppErrorCodes` của notification. Thêm `@LogActivity` khi endpoint cần AOP logging/exception conversion; endpoint demo hiện chưa có annotation này.

## Dispatch

`NotifyService.sendNotify` lấy sender theo `ENotifyMethod`, build content nếu `ETemplateNotify` có template id, gọi `sender.send`, rồi chỉ schedule follow-up qua `TaskExecutor` sau khi gửi thành công. Follow-up có thể lưu notification và kiểm tra balance SMS/Zalo.

Khi mở rộng:

1. Chỉ thêm enum/template/DTO field khi channel contract cần.
2. Đăng ký sender trong `MessageSenderFactory`; có enum không đồng nghĩa sender đã hoạt động.
3. Tách template rendering khỏi transport.
4. Không log credential, token, OTP hay full payload nhạy cảm.
5. Xác định timeout, non-2xx, malformed response và retry cho từng external call.

Email dùng Spring Mail. SMS/ZNS dùng Java `HttpClient` và `SmsProperties`/`ZnsProperties`, cấu hình dưới `spring.sms`/`spring.zns`. `AppSender` là stub. Phải kiểm tra return value hiện tại vì request attempt không đồng nghĩa delivery thành công.

Test sender selection, method lạ, template/no-template, async follow-up, request external, timeout/failure và kết quả public; dùng mock, không dùng credential thật.
