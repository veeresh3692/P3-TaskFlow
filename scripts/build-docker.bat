@echo off
REM =========================================================================
REM TASKFLOW - Build All Docker Images
REM =========================================================================

where docker >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Docker is not installed or not in your PATH.
    echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop/
    exit /b 1
)

echo [1/5] Building Monolith Backend Image (taskflow-backend:latest)...
docker build -t taskflow-backend:latest -f backend/Dockerfile .
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo [2/5] Building React Frontend Image (taskflow-frontend:latest)...
docker build -t taskflow-frontend:latest -f frontend/Dockerfile ./frontend
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo [3/5] Building User Service Image (taskflow-user-service:latest)...
docker build -t taskflow-user-service:latest -f services/user-service/Dockerfile .
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo [4/5] Building Task Service Image (taskflow-task-service:latest)...
docker build -t taskflow-task-service:latest -f services/task-service/Dockerfile .
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo [5/5] Building Notification Service Image (taskflow-notification-service:latest)...
docker build -t taskflow-notification-service:latest -f services/notification-service/Dockerfile .
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo.
echo =========================================================================
echo All TASKFLOW Docker images built successfully!
echo To run the full system, execute: docker compose up
echo =========================================================================
