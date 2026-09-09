---
name: springboot-lib-cdn
description: 'Implement or debug CDN metadata, file upload, local file serving, CDN routes, path handling, or multipart processing in this Spring Boot project.'
---

# CDN Workflow

## Ownership and Flow

CDN metadata belongs to `com.springboot.cdn` and persistence belongs to `com.springboot.jpa.domain.Cdn` plus `com.springboot.jpa.repository.cdn.CdnRepository`. The REST flow is `CdnController` for metadata/list endpoints and `FileController` for upload/serving, followed by `CdnServiceImpl` or `FileServiceImpl`, mapper, and repository.

Use constructor injection, `@RestController`, route constants/base controllers where already present, `ResponseEntity<?>`, and `ControllerHelper.success(...)`. Use `@LogActivity` on endpoints that should receive the repository's request logging and localized error behavior.

## Upload Rules

`FileServiceImpl` reads `cdn.base-path` with default `app/cdn`, creates `<env>/<domain>/<group>`, cleans the original filename, preserves its extension, generates a SHA-256/UUID-derived 32-character name, copies the multipart stream, and saves the relative link in `Cdn`.

When changing upload behavior:

1. Validate missing/empty multipart content and all enum/group/status inputs.
2. Use `Path` APIs and absolute normalized paths; do not build filesystem paths by unsafe string concatenation.
3. Keep the filesystem target, persisted link, route format, and DTO fields consistent.
4. Decide and test overwrite, duplicate upload, extension, and directory-creation semantics.
5. Keep file errors in `com.springboot.cdn.exception.AppErrorCodes` and throw `AppException`.

## File Serving Security

`getFile` must enforce containment, not just call `normalize()`. Resolve the requested path against the configured absolute CDN base, normalize both paths, and reject any result that is outside the base directory before creating a `UrlResource`. Test traversal using `..`, absolute paths, encoded separators where applicable, missing files, and unreadable files.

## Verification

Test successful upload, empty upload, invalid metadata, missing file, traversal rejection, and response envelope. Keep CDN changes separate from notification code; they have different dependencies and failure semantics.
