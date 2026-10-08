# TASKFLOW — Semester 2: Security, Filtering, Docker & CI/CD

## 1. Overview & Objectives

Semester 2 extends the monolithic application into an enterprise-ready system with production-grade security, advanced Jira-style filtering, containerization, and automated CI/CD pipelines.

### Key Deliverables Completed:
1. **Spring Security & JWT Authentication:** Stateless token-based authentication with HMAC-SHA256 signatures and BCrypt password encryption.
2. **Role-Based Access Control (RBAC):** Three distinct roles (`ADMIN`, `MANAGER`, `MEMBER`) enforced via `@PreAuthorize` annotations and Spring Security filter chains.
3. **Jira-Style Dynamic Task Filtering:** Specification-based criteria queries supporting any combination of filters, full-text search, pagination, and sorting.
4. **Environment Variables & Secrets Management:** No hard-coded credentials; externalized configuration via `.env.example` and Spring environment interpolation.
5. **Docker Containerization:** Multi-stage production `Dockerfile`s and `docker-compose.monolith.yml`.
6. **Automated CI/CD Pipeline:** Multi-job GitHub Actions workflow (`.github/workflows/ci.yml`) validating Java tests, frontend builds, and Docker compose configurations.

---

## 2. Authentication & Authorization (JWT + RBAC)

### Architecture
- **Stateless Tokens:** When a user logs in, the backend issues an HMAC-SHA256 signed JSON Web Token containing `sub` (email), `role`, `id`, `iat`, and `exp`.
- **`JwtAuthFilter`:** A custom `OncePerRequestFilter` intercepts incoming HTTP requests, extracts the `Authorization: Bearer <token>` header, verifies the signature, and populates the `SecurityContextHolder`.
- **Password Security:** All passwords are encrypted with `BCryptPasswordEncoder` (10 rounds); plaintext passwords are never persisted or logged.

### Role Authorization Enforcement

```java
// Example: Restricting project deletion to Administrators
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
    projectService.deleteProject(id);
    return ResponseEntity.noContent().build();
}

// Example: Allowing Managers and Admins to manage Sprints
@PostMapping
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public ResponseEntity<SprintResponse> createSprint(@Valid @RequestBody SprintRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(sprintService.createSprint(request));
}
```

---

## 3. Jira-Style Dynamic Task Filtering & Pagination

Instead of creating exponentially complex repository methods (`findByStatusAndPriorityAndProjectId...`), TASKFLOW leverages the Spring Data JPA `Specification` API (`TaskSpecification.java`):

```java
public static Specification<Task> filter(
        Long projectId,
        Long sprintId,
        Long assigneeId,
        TaskStatus status,
        TaskPriority priority,
        String search) {
    return (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (projectId != null) {
            predicates.add(cb.equal(root.get("project").get("id"), projectId));
        }
        if (sprintId != null) {
            predicates.add(cb.equal(root.get("sprint").get("id"), sprintId));
        }
        if (assigneeId != null) {
            predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            predicates.add(cb.equal(root.get("priority"), priority));
        }
        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
            Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
            predicates.add(cb.or(titleMatch, descMatch));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}
```

### Combined Filter Capabilities:
- Any subset of query parameters can be combined in a single request.
- Integrates seamlessly with Spring Data `Pageable` and `Sort`:
  `GET /api/tasks?projectId=1&status=IN_PROGRESS&priority=HIGH&search=auth&page=0&size=10&sort=dueDate,asc`

---

## 4. Docker Containerization & Orchestration

### Multi-Stage Monolith Dockerfile (`backend/Dockerfile`)
1. **Builder Stage (`maven:3.9.6-eclipse-temurin-21-alpine`):** Compiles sources and packages the executable boot JAR.
2. **Runtime Stage (`eclipse-temurin:21-jre-alpine`):** Copies only the built JAR, sets non-root security permissions, and executes with JVM container memory flags.

### Monolith Orchestration (`docker-compose.monolith.yml`)
- Orchestrates PostgreSQL 16 (`taskflow-monolith-db`), Spring Boot Monolith backend (`taskflow-monolith-backend`), and React Frontend (`taskflow-monolith-frontend`).
- Employs Docker health checks (`pg_isready`) and dependent sequencing (`condition: service_healthy`).

---

## 5. Continuous Integration (GitHub Actions)

The workflow file `.github/workflows/ci.yml` runs on every push and pull request:
1. **`backend-test-and-package`:** Sets up Java 21, runs full unit & integration tests, packages JARs.
2. **`frontend-test-and-build`:** Sets up Node 20, executes frontend unit tests, validates clean production Vite bundle generation.
3. **`docker-compose-validate`:** Validates syntax and configuration of both monolith and microservices compose files.
