# ShopSample Spring WebFlux API

A reactive REST API built with **Kotlin**, **Spring Boot WebFlux**, and **MongoDB** demonstrating product management with coroutines, role-based access control, and Swagger documentation.

---

## Features

- Product management via REST endpoints (`/api/v1/products`)
- HTTP Basic authentication with two hardcoded roles: `ROLE_ADMIN`, `ROLE_USER`
- Method-level security via `@PreAuthorize`
- Bean Validation on request bodies
- Swagger UI documentation
- Paged product listing
- Asynchronous image fetching from [picsum.photos](https://picsum.photos) on product creation (fire-and-forget, does not block the request)
- Centralized exception handling (`GlobalExceptionHandler`)
- Docker Compose setup with MongoDB

---

## Tech Stack

| Layer       | Technology                                      |
|-------------|------------------------------------------------|
| Language    | Kotlin                                          |
| Framework   | Spring Boot 3 / WebFlux (reactive + coroutines) |
| Database    | MongoDB 8                                       |
| Auth        | HTTP Basic (Spring Security)                    |
| Docs        | SpringDoc OpenAPI / Swagger UI                  |
| Build       | Maven Wrapper                                   |
| Container   | Docker / Docker Compose                         |

---

## API Endpoints

### Products — `/api/v1/products`

| Method   | Path                        | Role          | Description                    |
|----------|-----------------------------|---------------|--------------------------------|
| `POST`   | `/api/v1/products`          | ADMIN         | Create a new product           |
| `GET`    | `/api/v1/products/{id}`     | ADMIN / USER  | Get product by ID              |
| `DELETE` | `/api/v1/products/{id}`     | ADMIN         | Delete product by ID           |
| `GET`    | `/api/v1/products/paging`   | ADMIN / USER  | Get all products with paging   |

### Other

| Path                     | Auth required | Description       |
|--------------------------|---------------|-------------------|
| `/actuator/health`       | No            | Health check      |
| `/swagger-ui.html`       | No            | Swagger UI        |
| `/api-docs`              | No            | OpenAPI JSON docs |

---

## Authentication

HTTP Basic authentication is used. Two users are pre-configured:

| Username | Password   | Role         |
|----------|------------|--------------|
| `admin`  | `password` | `ROLE_ADMIN` |
| `user`   | `password` | `ROLE_USER`  |

---

## Request Body — Create Product

```json
{
  "name": "Laptop",
  "description": "High-performance laptop with 16GB RAM",
  "sku": "SKU-12345",
  "price": 999.99
}
```

| Field         | Type       | Constraints                                         |
|---------------|------------|-----------------------------------------------------|
| `name`        | String     | Required, 3–100 characters                          |
| `description` | String     | Required                                            |
| `sku`         | String     | Required, unique, uppercase letters / numbers / `-` |
| `price`       | BigDecimal | Required, minimum `0.01`                            |

---

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- MongoDB running at `mongodb://localhost:27017/shop_sample`
- Docker & Docker Compose (optional)

---

## Running the Application

### Option 1 — Docker Compose

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/shop-sample-spring-webflux-api.git
   cd shop-sample-spring-webflux-api
   ```

2. Create a `.env` file in the project root:
   ```env
   MONGO_ROOT_USERNAME=root
   MONGO_ROOT_PASSWORD=secret
   MONGO_DATABASE=shop_sample
   ```

3. Start the application and MongoDB:
   ```bash
   docker compose up --build
   ```

4. To stop the application and remove containers:
   ```bash
   docker compose down
   ```
   To also remove volumes (deletes all MongoDB data):
   ```bash
   docker compose down -v
   ```

### Option 2 — Local Maven

1. Make sure MongoDB is running locally on port `27017`.

2. Clone and build:
   ```bash
   git clone https://github.com/your-username/shop-sample-spring-webflux-api.git
   cd shop-sample-spring-webflux-api
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

---

## Useful URLs

| URL                                      | Description         |
|------------------------------------------|---------------------|
| `http://localhost:8080/swagger-ui.html`  | Swagger UI          |
| `http://localhost:8080/api-docs`         | OpenAPI JSON        |
| `http://localhost:8080/actuator/health`  | Health check        |

---

## Build & Test

```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ShopSampleApplicationTests

# Skip tests during build
./mvnw clean package -DskipTests
```

---

## Project Structure

```
src/main/kotlin/com/mp/webflux/api/shopsample/
├── controller/       # REST endpoints
├── service/          # Business logic (interfaces + implementations)
├── repository/       # Spring Data MongoDB repositories
├── document/         # MongoDB document models
├── dto/              # Request / response DTOs
├── config/           # Spring beans (Security, WebClient, Swagger, WebFlux)
├── exception/        # Custom exception types
└── util/             # GlobalExceptionHandler
```

---

## License

This project is licensed under the [MIT License](LICENSE).