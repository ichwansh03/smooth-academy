---
name: backend
description: >
  Quarkus backend for smooth-academy. Hibernate ORM with Panache, Flyway migrations,
  PostgreSQL. REST API at /api. Used for managing quiz content, student results,
  and premium subscriptions.
---

## Stack

- Java 21, Quarkus 3.37, Maven
- RESTEasy REST + Hibernate ORM Panache + Flyway + PostgreSQL
- Swagger UI at `/q/swagger-ui`

## Project Map

```
smooth-academy/
├── pom.xml
├── docker-compose.yml          # PostgreSQL + App
└── src/main/
    ├── docker/Dockerfile.jvm
    ├── resources/
    │   ├── application.properties
    │   └── db/migration/        # Flyway migrations
    └── java/org/ichwan/
        ├── entity/              # JPA entities
        └── resource/            # REST resources (PanacheRepositoryBase)
```

## Conventions

### Entities (`entity/`)
- Use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`)
- UUID primary keys: `@GeneratedValue(strategy = GenerationType.UUID)`
- Integer primary keys for lookup tables (Level): manual assign
- `@PrePersist` / `@PreUpdate` for timestamps
- Table/column names: snake_case via `@Table(name = "snake_case")` and `@Column(name = "snake_case")`
- Use `Instant` for timestamps
- Boolean fields: `isPremium` -> column `is_premium`, getter `isPremium()`

### Resources (`resource/`)
- Extend `PanacheRepositoryBase<Entity, IdType>` for repository + REST
- All paths under `/api` (set in `application.properties`)
- `@ApplicationScoped` + `@Path` + `@Produces`/`@Consumes(MediaType.APPLICATION_JSON)`
- Cross-entity lookups: `@Inject OtherResource` (they are also repositories)
- `@Transactional` on write operations
- Return `Response.created(URI)` for POST, `Response.status(400)` for errors

### Flyway
- Migrations in `src/main/resources/db/migration/`
- Naming: `V{number}__{description}.sql`
- `application.properties`: `quarkus.flyway.migrate-at-start=true`
- Hibernate DDL: `quarkus.hibernate-orm.database.generation=none` (Flyway manages schema)

### Profiles
- Default: container mode (db host = `db`, the Docker service name)
- `%dev`: localhost DB, DEBUG logging
- Run dev: `./mvnw quarkus:dev` (auto-reload). Requires PG on localhost:5432.

## Domain Model

### Educational Levels

Elementary Level A — Basic Calculation:

| Level | Operation | Sub-levels |
|-------|-----------|------------|
| 1 | Addition | a. ones, b. tens, c. hundreds, d. thousands |
| 2 | Subtraction | a. ones, b. tens, c. hundreds, d. thousands |
| 3 | Multiplication | a. ones, b. tens, c. hundreds, d. thousands |
| 4 | Division | a. ones, b. tens, c. hundreds, d. thousands |
| 5 | Mixed Operations | a. ones, b. tens, c. hundreds, d. thousands |

Each sub-level means the numbers used fall in that magnitude (1-9, 10-99, 100-999, 1000-9999).

### Current Tables

| Table | Purpose |
|-------|---------|
| `users` | Students + premium subscription tracking |
| `levels` | 4 levels (will expand to 5 with sub-levels) |
| `quiz_results` | Quiz attempt results per user per level |
| `quiz_answers` | Per-question detail within a quiz |

### Planned Tables (not yet implemented)

- **`questions`** — Bank of predefined questions with operation type, difficulty level, correct answer, distractors. Enables CRUD for quiz content.
- **`sub_levels`** — Sub-levels (a-d) within each level, with min/max range per operation.
- **`premium_plans`** — Subscription plans (monthly/yearly), pricing, feature access mapping.

## API Endpoints

### Users
- `POST /api/users/register` — Register (email, password, displayName)
- `GET /api/users/by-email/{email}` — Lookup by email
- `GET /api/users/{id}` — Get by UUID

### Levels
- `GET /api/levels` — List all, ordered by sort_order
- `GET /api/levels/{id}` — Get by ID

### Quiz Results
- `POST /api/quiz-results` — Submit result (userId, levelId, mode, totalQuestions, correctCount)
- `GET /api/quiz-results/user/{userId}` — History for user, newest first
- `GET /api/quiz-results/{id}` — Get by ID

### Future
- `GET/POST/PUT/DELETE /api/questions` — Manage question bank (admin)
- `GET /api/reports/student/{userId}` — Student performance report
- `GET /api/reports/class` — Aggregate class/school report

## Premium Features

Column `is_premium` on `users` table. Future gating:
- Unlimited quiz attempts (vs. daily limit for free)
- Detailed per-question answer review
- PDF certificate generation
- Parent progress reports via email
- No ads

## Error Handling

- `throw new WebApplicationException(statusCode)` for simple errors
- Future: `@ExceptionMapper` for structured error JSON response

## Testing

- Run tests: `./mvnw test`
- Integration tests: QuarkusTest + RestAssured
