# TASKFLOW — REST API Documentation

## Overview

TASKFLOW REST APIs adhere to standard RESTful conventions:
- **Base URL (Monolith):** `http://localhost:8080/api`
- **Base URL (Microservices via Gateway):** `http://localhost:3000/api` or `http://localhost:80/api`
- **User Service Direct:** `http://localhost:8081/api`
- **Task Service Direct:** `http://localhost:8082/api`
- **Notification Service Direct:** `http://localhost:8083/api`
- **Content Type:** `application/json`
- **Authentication:** `Authorization: Bearer <JWT_TOKEN>`

---

## Standard Error Response Format

All APIs return a uniform error envelope during exceptions:

```json
{
  "timestamp": "2026-10-08T18:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Task title is required and must be between 3 and 200 characters",
  "path": "/api/tasks",
  "validationErrors": {
    "title": "size must be between 3 and 200"
  }
}
```

---

## 1. Authentication Endpoints (`/api/auth`)

### 1.1 Register New User
- **Method:** `POST`
- **Path:** `/api/auth/register`
- **Access:** Public

**Request Body:**
```json
{
  "name": "Sarah Connor",
  "email": "sarah@taskflow.com",
  "password": "Password123!",
  "role": "MEMBER"
}
```

**Response (`201 Created`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "type": "Bearer",
  "id": 4,
  "name": "Sarah Connor",
  "email": "sarah@taskflow.com",
  "role": "MEMBER"
}
```

### 1.2 User Login
- **Method:** `POST`
- **Path:** `/api/auth/login`
- **Access:** Public

**Request Body:**
```json
{
  "email": "admin@taskflow.com",
  "password": "admin123"
}
```

**Response (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "type": "Bearer",
  "id": 1,
  "name": "System Administrator",
  "email": "admin@taskflow.com",
  "role": "ADMIN"
}
```

### 1.3 Get Authenticated User Profile
- **Method:** `GET`
- **Path:** `/api/auth/me`
- **Access:** Authenticated (`ADMIN`, `MANAGER`, `MEMBER`)

**Response (`200 OK`):**
```json
{
  "id": 1,
  "name": "System Administrator",
  "email": "admin@taskflow.com",
  "role": "ADMIN"
}
```

---

## 2. Project Endpoints (`/api/projects`)

### 2.1 List All Projects
- **Method:** `GET`
- **Path:** `/api/projects`
- **Access:** Authenticated

**Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "name": "TaskFlow Core Platform",
    "description": "Enterprise agile task and project management ecosystem",
    "createdAt": "2026-10-08T18:00:00Z",
    "updatedAt": "2026-10-08T18:00:00Z"
  }
]
```

### 2.2 Create Project
- **Method:** `POST`
- **Path:** `/api/projects`
- **Access:** `ADMIN`, `MANAGER`

**Request Body:**
```json
{
  "name": "Mobile Client Application",
  "description": "iOS and Android client built with React Native"
}
```

### 2.3 Get Project by ID
- **Method:** `GET`
- **Path:** `/api/projects/{id}`
- **Access:** Authenticated

### 2.4 Update Project
- **Method:** `PUT`
- **Path:** `/api/projects/{id}`
- **Access:** `ADMIN`, `MANAGER`

### 2.5 Delete Project
- **Method:** `DELETE`
- **Path:** `/api/projects/{id}`
- **Access:** `ADMIN`

---

## 3. Sprint Endpoints (`/api/sprints`)

### 3.1 List All Sprints (Optionally by Project)
- **Method:** `GET`
- **Path:** `/api/sprints?projectId=1`
- **Access:** Authenticated

**Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "name": "Sprint 1: Foundation & Authentication",
    "goal": "Deliver core security, project scaffolding, and task data models",
    "startDate": "2026-10-01T09:00:00Z",
    "endDate": "2026-10-15T18:00:00Z",
    "status": "ACTIVE",
    "projectId": 1,
    "createdAt": "2026-10-08T18:00:00Z",
    "updatedAt": "2026-10-08T18:00:00Z"
  }
]
```

### 3.2 Create Sprint
- **Method:** `POST`
- **Path:** `/api/sprints`
- **Access:** `ADMIN`, `MANAGER`

**Request Body:**
```json
{
  "name": "Sprint 2: Kanban & Kafka Integration",
  "goal": "Implement dynamic Kanban status boards and async event streaming",
  "startDate": "2026-10-16T09:00:00Z",
  "endDate": "2026-10-30T18:00:00Z",
  "status": "PLANNED",
  "projectId": 1
}
```

### 3.3 Update Sprint Status
- **Method:** `PUT` / `PATCH`
- **Path:** `/api/sprints/{id}`
- **Access:** `ADMIN`, `MANAGER`

---

## 4. Task Endpoints & Jira-Style Filtering (`/api/tasks`)

### 4.1 Search and Filter Tasks
- **Method:** `GET`
- **Path:** `/api/tasks`
- **Access:** Authenticated

**Query Parameters:**
| Parameter | Type | Description | Example |
| :--- | :--- | :--- | :--- |
| `projectId` | Long | Filter by associated project | `?projectId=1` |
| `sprintId` | Long | Filter by associated sprint | `?sprintId=1` |
| `assigneeId` | Long | Filter by assigned user ID | `?assigneeId=3` |
| `status` | Enum | Filter by task status (`TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`) | `?status=IN_PROGRESS` |
| `priority` | Enum | Filter by priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) | `?priority=CRITICAL` |
| `search` | String | Case-insensitive substring match in title or description | `?search=database` |
| `page` | Integer | Zero-indexed page index (default: `0`) | `?page=0` |
| `size` | Integer | Items per page (default: `20`) | `?size=10` |
| `sort` | String | Format: `field,direction` (default: `id,desc`) | `?sort=dueDate,asc` |

**Multi-filter Combination Example:**
```http
GET /api/tasks?projectId=1&sprintId=1&status=IN_PROGRESS&priority=HIGH&search=OAuth&page=0&size=10&sort=createdAt,desc
```

**Response (`200 OK` - Spring Data Page):**
```json
{
  "content": [
    {
      "id": 101,
      "title": "Configure OAuth2 JWT Security Filter",
      "description": "Implement stateless JWT authentication with BCrypt password encoder",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "dueDate": "2026-10-14T23:59:59Z",
      "projectId": 1,
      "sprintId": 1,
      "assigneeId": 3,
      "assigneeName": "Alex Member",
      "createdAt": "2026-10-08T18:00:00Z",
      "updatedAt": "2026-10-08T18:15:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### 4.2 Create Task
- **Method:** `POST`
- **Path:** `/api/tasks`
- **Access:** Authenticated

**Request Body:**
```json
{
  "title": "Set up Flyway Database Migrations",
  "description": "Add V1, V2, and V3 migrations for tables, indexes, and constraints",
  "status": "TODO",
  "priority": "MEDIUM",
  "dueDate": "2026-10-20T18:00:00Z",
  "projectId": 1,
  "sprintId": 1,
  "assigneeId": 3
}
```

### 4.3 Update Task Status (Kanban Transition)
- **Method:** `PATCH`
- **Path:** `/api/tasks/{id}/status`
- **Access:** Authenticated

**Request Body:**
```json
{
  "status": "DONE"
}
```

**Response (`200 OK`):**
Returns updated `TaskResponse`. In microservices mode, also automatically dispatches a `TASK_STATUS_CHANGED` event to Kafka.

### 4.4 Delete Task
- **Method:** `DELETE`
- **Path:** `/api/tasks/{id}`
- **Access:** `ADMIN`, `MANAGER`

---

## 5. Notification Endpoints (`/api/notifications`)

### 5.1 Get User Notifications
- **Method:** `GET`
- **Path:** `/api/notifications?userId=3`
- **Access:** Authenticated

**Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "userId": 3,
    "title": "Task Assigned",
    "message": "You have been assigned to task: \"Set up Flyway Database Migrations\"",
    "taskId": 102,
    "type": "TASK_ASSIGNED",
    "isRead": false,
    "createdAt": "2026-10-08T18:20:00Z"
  }
]
```

### 5.2 Mark Notification as Read
- **Method:** `PATCH`
- **Path:** `/api/notifications/{id}/read`
- **Access:** Authenticated

### 5.3 Get Unread Count
- **Method:** `GET`
- **Path:** `/api/notifications/unread-count?userId=3`
- **Access:** Authenticated

**Response (`200 OK`):**
```json
{
  "unreadCount": 1
}
```

---

## 6. System Health & Observability Endpoints (`/actuator`)

Spring Boot Actuator endpoints provide real-time runtime monitoring:
- `GET /actuator/health`: System health status (`UP`/`DOWN`).
- `GET /actuator/info`: Application version, Git commit, build details.
- `GET /actuator/metrics`: JVM memory consumption, active threads, HTTP request latencies.
- `GET /swagger-ui/index.html` or `/swagger-ui.html`: Interactive OpenAPI 3.0 UI.
