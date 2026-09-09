---
name: springboot-lib-infrastructure
description: 'Configure, run, or troubleshoot MySQL, JPA, Redis, Docker Compose, profiles, environment variables, cache, or duplicate-request protection for this Spring Boot project.'
---

# Infrastructure and Cache Workflow

## Runtime Configuration

- `application.yaml` activates profile `dev` and defaults the server to `localhost:9000`.
- `application-dev.yml` defaults MySQL to `localhost:3307` and Redis to `localhost:6379`, database `5`.
- `DatabaseConfiguration` binds `spring.datasource.springboot.jdbc-url`, username, password, driver, and Hikari settings. Standard `spring.datasource.url` alone does not configure this bean.
- Hibernate uses `ddl-auto: update`; inspect the entity mappings before trusting the checked-in SQL initialization script.

## Redis and Cache

`RedisConfig` explicitly creates a Lettuce factory, `RedisTemplate<String, String>`, listener container, and `main-single-request` Lua script. `RedisService` handles values, hashes, increments, TTL, and duplicate request protection. Check `RedisConstant` before inventing keys. Some hash helpers assume a value exists and need null-safe handling at call sites.

`CacheService` is an in-memory bounded TTL/LRU cache with cleanup and statistics. `LazyCache` reloads values on time expiry and is used by feature caches. Preserve thread-safety, expiry semantics, and cache invalidation behavior when modifying them.

## Docker Mismatch Check

The compose app currently exports standard `SPRING_DATASOURCE_*`/`SPRING_DATA_REDIS_*`, defaults to profile `prod`, and exposes port `8080`. The Java app expects the custom datasource namespace, profile `dev`, and port `9000`. Reconcile these explicitly before using compose for the app.

## Verification

1. Confirm every property consumed by configuration beans exists in the active profile.
2. Test cache hit, miss, expiry, eviction, and duplicate-request TTL behavior.
3. Use integration tests only with MySQL/Redis available.
4. Run `./mvnw.cmd test` from the repository root and distinguish compile success from connection verification.
