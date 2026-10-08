# TASKFLOW — Semester 1: Monolithic Foundation & CRUD

## 1. Overview & Objectives

In Semester 1, the objective is to build a robust, clean, full-stack CRUD application for agile task and project management.

### Key Deliverables Completed:
1. **Spring Boot Monolithic Backend:** Layered MVC architecture (`controller`, `service`, `repository`, `entity`, `dto`, `exception`).
2. **Task CRUD API:** Full create, read, update, delete endpoints plus status transition PATCH.
3. **Sprint CRUD API:** Full lifecycle management linked to projects.
4. **Project CRUD API:** Multi-project organization with cascading relations.
5. **Modern React Frontend:** Interactive Kanban board (4 columns: `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`), project dashboard, sprint planning, and modal dialogs.
6. **Input Validation:** Backend `@Valid` Bean Validation and frontend form error messages.
7. **Global Error Handling:** `@ControllerAdvice` (`GlobalExceptionHandler`) providing uniform JSON error responses.
8. **Database Persistence:** Spring Data JPA with Flyway schema migration scripts.
9. **Interactive API Documentation:** Springdoc OpenAPI 3 / Swagger UI (`/swagger-ui.html`).

---

## 2. Domain Model & Entities

```
+----------------+          +-----------------+          +-----------------+
|    Project     | 1      N |     Sprint      | 1      N |      Task       |
|----------------|----------|-----------------|----------|-----------------|
| id             |          | id              |          | id              |
| name           |          | name            |          | title           |
| description    |          | goal            |          | description     |
| createdAt      |          | startDate       |          | status          |
| updatedAt      |          | endDate         |          | priority        |
+----------------+          | status          |          | dueDate         |
                            | projectId       |          | projectId       |
                            | createdAt       |          | sprintId        |
                            | updatedAt       |          | assigneeId      |
                            +-----------------+          | createdAt       |
                                                         | updatedAt       |
                                                         +-----------------+
```

### Enums
- **`TaskStatus`:** `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`
- **`TaskPriority`:** `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
- **`SprintStatus`:** `PLANNED`, `ACTIVE`, `COMPLETED`
- **`Role`:** `ADMIN`, `MANAGER`, `MEMBER`

---

## 3. Backend Implementation Highlights

### Controller Layer
- `ProjectController.java` (`/api/projects`): RESTful operations for project management.
- `SprintController.java` (`/api/sprints`): Sprint scheduling and status updates.
- `TaskController.java` (`/api/tasks`): Task creation, editing, status patching, and retrieval.
- `AuthController.java` (`/api/auth`): Registration, login, and profile fetching.

### Validation Rules
- **Task Title:** `@NotBlank`, length between 3 and 200 characters.
- **Project Name:** `@NotBlank`, length between 2 and 120 characters.
- **Sprint Dates:** Invariant check ensuring `endDate` is not chronologically earlier than `startDate`.
- **Email:** `@Email`, required format check.

### Global Exception Handler (`GlobalExceptionHandler.java`)
Intercepts exceptions across all controllers:
- `MethodArgumentNotValidException` $\to$ Returns `400 Bad Request` with a detailed field-by-field map of validation failures.
- `ResourceNotFoundException` $\to$ Returns `404 Not Found` with the missing entity description.
- `IllegalArgumentException` / `IllegalStateException` $\to$ Returns `400 Bad Request`.
- `BadCredentialsException` $\to$ Returns `401 Unauthorized`.
- `AccessDeniedException` $\to$ Returns `403 Forbidden`.
- `Exception` $\to$ Returns `500 Internal Server Error` without exposing stack traces.

---

## 4. Frontend Implementation Highlights

### Reusable Component Architecture
- **`Navbar.jsx`:** Brand identity, navigation tabs, unread notification badge, logged-in user profile, and logout trigger.
- **`TaskBoardPage.jsx`:** 4-column agile Kanban board with real-time status transitions (`TODO` $\leftrightarrow$ `IN_PROGRESS` $\leftrightarrow$ `IN_REVIEW` $\leftrightarrow$ `DONE`).
- **`FilterBar.jsx`:** Interactive filter controls (project select, sprint select, assignee select, status filter, priority filter, and search input).
- **`TaskModal.jsx`:** Reusable dialog for creating and editing tasks with live validation.
- **`SprintModal.jsx`:** Dialog for configuring sprint goals and date ranges.
- **`ProjectModal.jsx`:** Dialog for project creation.
- **`Dashboard.jsx`:** Visual metrics, completion progress bars, priority distribution, and recent task activity.

---

## 5. Verification & Tests

To execute Semester 1 tests:
```bash
.\mvnw.cmd test -pl backend -Dtest=TaskControllerTest,TaskServiceTest,ProjectControllerTest
```
All unit tests mock service layers via Mockito and verify HTTP response codes, JSON serialization, and error handling.
