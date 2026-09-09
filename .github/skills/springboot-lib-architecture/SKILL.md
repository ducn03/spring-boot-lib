---
name: springboot-lib-architecture
description: 'Understand and modify this Spring Boot library application. Use when navigating packages, tracing startup or bean wiring, choosing an extension point, diagnosing cross-module behavior, or planning changes across com.springboot.'
---

# Spring Boot Library Architecture

## Project Contract

This is a Spring Boot 3.4.3 / Java 17 application whose entry point is `com.springboot.prj.LibApplication`. `@ComponentScan("com.springboot")` makes that package the component boundary. New Spring beans belong below it unless configuration explicitly imports them.

The codebase is organized by responsibility:

- `com.springboot.lib`: shared DTOs, helpers, errors, AOP logging, cache, Redis, queues, and state machine.
- `com.springboot.jpa`: entities and Spring Data repositories.
- `com.springboot.prj`: sample application features, user/country/test flows, and the test EventBus handler.
- `com.springboot.cdn`: CDN metadata, upload, and file serving.
- `com.springboot.notification`: notification orchestration and channel integrations.
- `com.springboot.ws`: WebSocket connector, messages, sessions, and rooms.

## Local Coding Style

- Use constructor injection with `private final` dependencies.
- Keep controllers thin: receive request data, perform only basic null checks, call a service, and return `ControllerHelper.success(...)`.
- Return `ResponseEntity<?>` from REST controllers to preserve the existing response envelope.
- Keep DTO/entity conversion explicit in a feature `Mapper` with `toEntity` and `toDTO` methods; null input normally maps to null.
- Use Lombok logging consistently with the surrounding class (`@CustomLog` or `@Slf4j`), not ad hoc logger patterns.
- Preserve the existing package pattern: `features`, `service`, `service.<feature>.request`, `response`/`dto`, `cache`, and `route`.

## Change Procedure

1. Find the class that directly decides the behavior, not only the route or wiring class.
2. Trace `controller -> service -> mapper/repository/helper` and identify the response and error path.
3. Decide whether the code is reusable library behavior or a sample/demo; `UserServiceImpl` contains EntityManager/cache demonstrations and should not be copied as production design.
4. Preserve `ControllerHelper`, `ResponseData`, `AppException`, module error codes, `EStatus`, and localization conventions.
5. Add focused tests under `src/test/java`; current source test coverage is minimal.
6. Run `./mvnw.cmd test` from the repository root.

## Boundaries and Traps

- Kafka is only a dependency/stub. The working queue is synchronous in-process `EventBus`.
- `ChatRoomManager` and parts of notification are incomplete/demo code.
- `target/` is generated output, never the source of truth.
- Docker compose currently disagrees with the custom datasource namespace, profile, and port used by the Java application.
