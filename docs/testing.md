# TASKFLOW — Testing Strategy & Verification Guide

## 1. Testing Philosophy & Architecture

TASKFLOW employs a pyramid testing strategy covering unit tests, controller-level MockMvc integration tests, JPA specification verification, and frontend component tests.

```
       / \
      / E2E \       Manual / Postman / Swagger UI
     /-------\
    /  Integ  \     Spring Boot MockMvc, Controller & Repository Tests
   /-----------\
  /    Unit     \   Service business logic, Mockito, Vitest Component Tests
 /---------------\
```

---

## 2. Test Execution Commands

### Run Full Test Suite Across All Modules
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

### Run Tests for Specific Modules
```bash
# Monolith Backend
.\mvnw.cmd test -pl backend

# User Service
.\mvnw.cmd test -pl services/user-service

# Task Service
.\mvnw.cmd test -pl services/task-service

# Notification Service
.\mvnw.cmd test -pl services/notification-service

# Shared Common Library
.\mvnw.cmd test -pl shared/taskflow-common
```

### Run Frontend Unit Tests
```bash
cd frontend
npm test
```

---

## 3. Backend Test Suite Coverage

### Monolith Backend (`backend/src/test/`)
| Test Class | Focus / Scenarios Tested |
| :--- | :--- |
| **`TaskServiceTest`** | Task creation, status updates, JPA specification filtering, resource not found exceptions, business validation. |
| **`AuthServiceTest`** | User registration, password hashing verification, login credential matching, duplicate email rejection. |
| **`ProjectServiceTest`**| Project creation, name uniqueness, cascade retrieval. |
| **`SprintServiceTest`** | Sprint lifecycle transitions (`PLANNED` $\to$ `ACTIVE` $\to$ `COMPLETED`), date boundary validation. |
| **`TaskControllerTest`** | MockMvc integration test: HTTP 200/201 response codes, JSON serialization, `@Valid` bean validation failure on empty title (400 Bad Request), JWT security enforcement. |
| **`ProjectControllerTest`** | MockMvc tests for project CRUD and error envelope formatting. |

### Microservices Test Suites (`services/*/src/test/`)
| Module | Test Class | Focus / Scenarios Tested |
| :--- | :--- | :--- |
| **`user-service`** | `UserServiceTest` | User registration, authentication, JWT token generation, role verification. |
| **`task-service`** | `TaskServiceTest` | Task creation, Jira-style filtering, Kafka `TaskEvent` publishing invocation. |
| **`notification-service`** | `NotificationServiceTest` | Kafka `TaskEventConsumer` message handling, notification persistence, marking as read. |

---

## 4. Frontend Test Suite (`frontend/src/test/`)

Built with Vitest + React Testing Library:
- **`App.test.jsx`:** Verifies application rendering, authentication state transitions, route navigation, and protected route redirection to `/login`.
- **`Dashboard.test.jsx`:** Tests metric calculations, progress bar updates, and task distribution counters.

---

## 5. End-to-End Verification Scenarios

### Scenario 1: Authentication & RBAC
1. Register a new user with role `MEMBER` via `POST /api/auth/register`.
2. Login with valid credentials and verify JWT return.
3. Attempt to delete a project with role `MEMBER` $\to$ Verifies `403 Forbidden`.
4. Login with role `ADMIN` and delete the project $\to$ Verifies `204 No Content`.

### Scenario 2: Jira-Style Dynamic Filtering
1. Create tasks across multiple projects, sprints, priorities, and statuses.
2. Filter by status: `GET /api/tasks?status=IN_PROGRESS` $\to$ Only in-progress tasks returned.
3. Multi-filter combination: `GET /api/tasks?projectId=1&priority=HIGH&search=Kafka` $\to$ Dynamic SQL predicate matches precisely.
4. Verify pagination: `GET /api/tasks?page=0&size=2` $\to$ Returns exactly 2 items with total page metadata.

### Scenario 3: Asynchronous Kafka Event Streaming (Semester 3)
1. User #1 assigns a task to User #2 via `POST /api/tasks` in `task-service`.
2. `task-service` logs publication of `TaskEvent(eventType=TASK_ASSIGNED, assigneeId=2)`.
3. `notification-service` logs receipt of `TaskEvent` from topic `task-events`.
4. User #2 queries `GET /api/notifications?userId=2` $\to$ Sees real-time notification alert.
