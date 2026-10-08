# =========================================================================
# Multi-stage Dockerfile for TASKFLOW User Service
# Build: docker build -t taskflow-user-service -f services/user-service/Dockerfile .
# =========================================================================

# Stage 1: Build JAR with Maven & Eclipse Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY shared shared
COPY services/user-service services/user-service

RUN mvn clean package -DskipTests -pl services/user-service -am

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S taskflow && adduser -S taskflow -G taskflow
USER taskflow

COPY --from=builder /build/services/user-service/target/user-service-*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
