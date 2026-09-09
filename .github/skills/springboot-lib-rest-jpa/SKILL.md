---
name: springboot-lib-rest-jpa
description: 'Build or modify REST endpoints, services, DTOs, JPA entities, repositories, paging, soft deletion, validation, or localized API errors in this Spring Boot project.'
---

# REST and JPA Workflow

## REST Shape

Follow the existing controller pattern: `@RestController`, constructor-injected service, `ResponseEntity<?>`, and a route constant from the feature's `route` package. Return `ControllerHelper.success(data)` or `ControllerHelper.success(data, pagingData)` rather than constructing response JSON manually.

`ResponseData` contains `Meta` and `data`; successful responses use error code `OK`. Paging is created in the controller, populated from the request, and updated by the service with `PagingData.update(Page)`. Do not return a raw Spring `Page` when the feature uses this envelope.

Annotate endpoints with `@LogActivity` when they should receive request locale handling, asynchronous request/response logging, `HttpLog` persistence, and conversion of `AppException`/unexpected errors. There is no global `@ControllerAdvice`, so an endpoint without this aspect has different error behavior.

## Feature Layering

1. Put request objects under `service.<feature>.request` and output objects under `response` or `dto` according to the neighboring feature.
2. Keep controller validation limited to obvious request absence; put business validation in the service or validator.
3. Let the service return DTOs or typed lists, not entities.
4. Use a component mapper with explicit `toEntity` and `toDTO` assignments. Avoid leaking passwords or internal fields in `toDTO`.
5. Put derived queries in repository interfaces and dynamic filters in a JPA `Specification`.
6. Throw `AppException` with the owning module's `AppErrorCodes`/`ErrorCodes`; do not silently return null for a missing resource.

## JPA Rules

- Repositories are scanned from `com.springboot.jpa.repository`; entities from `com.springboot.jpa.domain`.
- `BaseEntity` supplies identity, optimistic `@Version`, integer `status`, and auditing timestamps.
- Use `EStatus.ACTIVE` in active queries and `EStatus.DELETED` for soft deletion.
- The transaction manager is `springbootTransactionManager` and the datasource namespace is `spring.datasource.springboot`.
- `@Transactional` is proxy-based; self-invocation, such as the demo `UserServiceImpl.testEM`, does not create a new transaction.

## Verification

For every endpoint change, inspect request binding, null/error behavior, mapper fields, repository filtering, status semantics, response envelope, and message localization together. Run `./mvnw.cmd test`; integration tests require the configured MySQL/Redis services.
