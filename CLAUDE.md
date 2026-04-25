# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ShopSampleApplicationTests

# Skip tests during build
./mvnw clean package -DskipTests
```

## Stack
- Kotlin + Spring Boot 4.x, WebFlux (reactive — NO blocking calls anywhere)
- Reactive MongoDB
- Maven

## Architecture

Spring Boot 4 WebFlux API (Kotlin, reactive + coroutines) backed by MongoDB. The stack uses `suspend` functions throughout rather than `Mono`/`Flux` where possible, with `ReactiveMongoTemplate` used for complex queries alongside `CoroutineCrudRepository`.

**Request flow:** Controller (suspend fun, `@PreAuthorize`) → `ProductServiceImpl` → `ProductRepository` (CoroutineCrudRepository) or `ReactiveMongoTemplate`

**Controllers:**
- `ProductController` — `GET /api/v1/products/**`, requires ADMIN or USER role
- `AdminProductController` — `POST/DELETE /api/v1/admin/products/**`, requires ADMIN role only

**Key design decisions:**
- Controllers use `suspend` functions — Spring WebFlux handles coroutine-to-reactive bridging automatically.
- `ProductServiceImpl` injects both `ProductRepository` (for simple CRUD) and `ReactiveMongoTemplate` (for paged/custom queries). When using `ReactiveMongoTemplate`, convert to coroutines with `.awaitSingle()` / `.awaitSingleOrNull()` or `.asFlow()`.
- `ImageService` uses a long-lived `CoroutineScope(Dispatchers.IO + SupervisorJob())` for fire-and-forget image fetching — image fetch failures must not fail product creation. `saveImageToS3` is currently a stub (logs + `delay(500)`).
- Security is in-memory HTTP Basic with two hardcoded users: `admin/password` (ROLE_ADMIN) and `user/password` (ROLE_USER). **Dual enforcement:** URL-level rules in `SecurityWebFilterChain` and method-level `@PreAuthorize` on controllers.
- Exception handling is centralized in `GlobalExceptionHandler` (`@RestControllerAdvice`). Custom exceptions: `ResourceNotFoundException` (404), `ResourceConflictException` (409). `DuplicateKeyException` from MongoDB is caught in `ProductServiceImpl` and rethrown as `ResourceConflictException`.
- `sku` field on `Product` has a unique MongoDB index.
- Logging uses `kotlin-logging`; logback is configured with `%X{traceId},%X{spanId}` MDC fields in the pattern (see `src/main/resources/logback.xml`). Application package logs at DEBUG, everything else at INFO.

**Packages:**
- `controller` — REST endpoints
- `service` — business logic interfaces + implementations
- `repository` — Spring Data Mongo repositories
- `document` — MongoDB document models
- `dto` — request/response DTOs
- `config` — Spring beans (Security, WebClient, Swagger, WebFlux)
- `exception` — custom exception types
- `util` — `GlobalExceptionHandler`

## External dependencies at runtime

- MongoDB at `mongodb://localhost:27017/shop_sample`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API docs: `http://localhost:8080/api-docs`
- Actuator health: `http://localhost:8080/actuator/health`
- `ImageService` calls `https://picsum.photos` to fetch placeholder images on product creation.

## Testing
- Unit tests: JUnit 5 + MockK (no Mockito)
- Integration tests: `@SpringBootTest` + Testcontainers, always in `src/test/`
- Naming: Given [context] When [action] Then [expected behavior]
- Example: `Given active order When cancelled Then status is CANCELLED`

## What NOT to do
- No blocking IO (.block(), runBlocking) — except in main() and test setup
- No field injection (`@Autowired` na field) — only constructor injection
- Never add new dependencies to maven pom.xml without explicit approval in this session

## Git
- Branches: `feat/`, `fix/`, `refactor/` prefix
- Commit: Conventional Commits (`feat: add cancellation flow`)
- Check `https://www.conventionalcommits.org/en/v1.0.0/`
- Use English language for commit messages