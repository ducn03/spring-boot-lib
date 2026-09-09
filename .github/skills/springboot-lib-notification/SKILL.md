---
name: springboot-lib-notification
description: 'Implement or debug notification orchestration, email, SMS, Zalo/ZNS, sender factories, notification templates, external HTTP calls, or notification routes in this Spring Boot project.'
---

# Notification Workflow

## Ownership and Flow

Notification code lives under `com.springboot.notification`. The route is handled by `features.notify.NotifyController`, orchestration by `service.notify.NotifyService`, sender selection by `MessageSenderFactory`, content construction by `NotifyBuilder`, and channel behavior by `channels.EmailSender`, `SmsSender`, `ZaloSender`, or `AppSender`.

Use constructor injection, `@RestController`, `ResponseEntity<?>`, and `ControllerHelper.success(...)` for REST responses. Use notification `AppErrorCodes` with `AppException` for invalid method/sender conditions. Add `@LogActivity` when the endpoint needs the repository's AOP logging and exception conversion; the current notify demo endpoint does not use it.

## Dispatch Rules

`NotifyService.sendNotify` obtains a sender from `ENotifyMethod`, builds content when `ETemplateNotify` has a non-empty template id, calls `sender.send`, and schedules follow-up work through `TaskExecutor` only after success. Follow-up work may save a notification and checks SMS/Zalo balance thresholds.

When extending this flow:

1. Add the method/template enum and DTO fields only when the channel contract needs them.
2. Register every sender in `MessageSenderFactory`; an enum value alone does not make a sender available.
3. Keep template rendering separate from transport calls.
4. Never log credentials, tokens, OTPs, or full sensitive message payloads.
5. Define timeout, non-2xx, malformed response, and retry behavior for every external call.

## Channels and Configuration

Email uses Spring Mail. SMS and ZNS/Zalo use Java `HttpClient` and property classes `SmsProperties`/`ZnsProperties`, configured under `spring.sms` and `spring.zns` in the active profile. `AppSender` is currently a stub. Existing SMS/Zalo and email return values must be verified before relying on them: a request attempt does not necessarily mean delivery succeeded.

## Verification

Test sender selection, unknown methods, template/no-template content, async follow-up scheduling, channel request construction, timeout/failure mapping, and externally visible return values. Use mocks for external providers and never require real credentials in tests.
