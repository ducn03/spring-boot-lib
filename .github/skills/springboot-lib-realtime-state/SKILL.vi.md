---
# Workflow Realtime và State

## WebSocket

Endpoint là `/webSocket`, cấu hình ở `WebsocketConfig`. Pipeline thật là `MessageDecoder -> SocketConnector -> SocketServer -> room manager`. `SocketConnector` parse JSON thủ công thành các subclass của `Message`; `SocketServer` route `CartMessage`, `ChatMessage`, `NotifyMessage`.

Khi sửa: giữ query string signature không rỗng nếu chưa thiết kế lại auth; để `SessionManager` quản lý lifecycle local và room manager quản lý membership Redis; kiểm tra synchronized access, disconnect cleanup và việc trả mutable internal set.

`CartRoomManager` và `NotifyRoomManager` có triển khai Redis hash + local session. `ChatRoomManager` hiện chưa triển khai, message type tồn tại không có nghĩa chat room hoạt động.

## EventBus

Queue đang hoạt động là EventBus synchronous trong process; `EventFactory` đăng ký handler `TEST`. `KafkaProducer` rỗng, không mô tả đây là Kafka durable/asynchronous.

## State machine

API reusable nằm trong `com.springboot.lib.sm`: `Template`, `State`, `Action`, `StateMachine`, `ManualStateMachine`, `SMInput`, `SMData`, `ActionCallback`. `ManualStateMachine.handle` load data, validate state/action, set next status, chạy callback và save trong `finally`.

Giữ error code riêng cho input/state/action thiếu. Test transition hợp lệ, transition sai, callback exception và persistence trong `finally`.
