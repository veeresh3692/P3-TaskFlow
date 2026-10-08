# =========================================================================
# Multi-stage Dockerfile for TASKFLOW Monolith Backend
# Build: docker build -t taskflow-backend -f backend/Dockerfile .
# =========================================================================

# Stage 1: Build JAR with Maven & Eclipse Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy parent and modules
COPY pom.xml .
COPY shared shared
COPY backend backend

# Build monolith artifact
RUN mvn clean package -DskipTests -pl backend -am

# Stage 2: Runtime image with minimal JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user for security best practices
RUN addgroup -S taskflow && adduser -S taskflow -G taskflow
USER taskflow

COPY --from=builder /build/backend/target/taskflow-backend-*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
