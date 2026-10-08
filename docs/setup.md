# TASKFLOW — Local Setup & Deployment Guide

This guide provides step-by-step instructions for running TASKFLOW locally, whether for rapid development, testing, grading, or production-like containerized deployment.

---

## 1. System Requirements & Prerequisites

| Tool | Recommended Version | Purpose |
| :--- | :--- | :--- |
| **Java Development Kit (JDK)** | 17, 21, or 25 | Compiling and running Spring Boot services |
| **Node.js & npm** | Node 18+ or 20+ (npm 9+) | Building and running React Vite frontend |
| **Maven** | 3.8+ (or included `mvnw` wrapper) | Multi-module build management |
| **Docker & Docker Compose** | Docker 24+ / Compose v2+ | Optional: containerized evaluation |
| **PostgreSQL** | 15+ or 16+ | Optional: production database evaluation |

---

## 2. Option A: Zero-Config Local Run (Recommended for Rapid Grading)

No external Docker or PostgreSQL installation is needed! The project includes a pre-configured `dev` profile that automatically uses an in-memory database with PostgreSQL dialect compatibility and auto-seeds sample accounts, projects, sprints, and tasks.

### Step 1: Start Monolith Backend (Port 8080)
Open a terminal in the project root:

```bash
# On Linux / macOS:
./mvnw spring-boot:run -pl backend

# On Windows (PowerShell / Command Prompt):
.\mvnw.cmd spring-boot:run -pl backend
```

The backend starts at `http://localhost:8080`.
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

### Step 2: Start React Frontend (Port 5173)
In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend launches at `http://localhost:5173` with instant Vite hot module replacement (HMR).

---

## 3. Option B: Docker Compose Microservices Run (Semester 3)

Runs PostgreSQL, Apache Kafka (KRaft mode), User Service (8081), Task Service (8082), Notification Service (8083), and the React Frontend + Nginx API Gateway (Port 3000):

```bash
# 1. Copy environment template
cp .env.example .env

# 2. Build and launch all containers
docker compose up --build
```

Access Points:
- **Web Application:** `http://localhost:3000`
- **User Service:** `http://localhost:8081`
- **Task Service:** `http://localhost:8082`
- **Notification Service:** `http://localhost:8083`
- **Kafka Broker:** `localhost:9092`
- **PostgreSQL:** `localhost:5432`

To shut down:
```bash
docker compose down -v
```

---

## 4. Option C: Docker Compose Monolith Run (Semester 2)

Runs PostgreSQL, the unified Spring Boot Monolith backend (8080), and the React Frontend (3000):

```bash
docker compose -f docker-compose.monolith.yml up --build
```

Access Points:
- **Web Application:** `http://localhost:3000`
- **Monolith Backend:** `http://localhost:8080`
- **PostgreSQL:** `localhost:5432`

---

## 5. Option D: Individual Microservices Local Run

If you want to run the microservices locally on separate terminals:

```bash
# Terminal 1: User Service (Port 8081)
.\mvnw.cmd spring-boot:run -pl services/user-service

# Terminal 2: Task Service (Port 8082)
.\mvnw.cmd spring-boot:run -pl services/task-service

# Terminal 3: Notification Service (Port 8083)
.\mvnw.cmd spring-boot:run -pl services/notification-service

# Terminal 4: Frontend (Port 5173)
cd frontend
npm run dev
```

---

## 6. Pre-Seeded Demo Accounts

The database seeder automatically initializes three demo accounts with distinct authorization levels:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@taskflow.com` | `admin123` | Full administrative control across projects, sprints, tasks, and users |
| **MANAGER** | `manager@taskflow.com` | `manager123` | Create & manage projects, sprints, and all team tasks |
| **MEMBER** | `member@taskflow.com` | `member123` | View projects/sprints, update status and edit assigned tasks |

> **Tip:** The frontend login screen features one-click demo account switcher buttons to instantly populate credentials for any role!
