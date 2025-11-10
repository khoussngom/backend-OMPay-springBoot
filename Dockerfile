# syntax=docker/dockerfile:1

# --- Build stage: compile the application with Maven ---
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only Maven files first for better caching
COPY pom.xml mvnw ./
RUN mkdir -p src && echo "" > src/.keep

# Copy sources
COPY src src

# Build the jar (skip tests by default in Docker build to speed up)
RUN mvn -B -DskipTests=true -U clean package

# --- Runtime stage: run the fat jar on JRE 17 ---
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Build arg to allow overriding jar name
ARG JAR_FILE=target/UsersMicroservice-0.0.1-SNAPSHOT.jar

# Copy jar from build stage
COPY --from=build /workspace/${JAR_FILE} ./app.jar

# Expose the application port (application.properties uses 8086 by default)
EXPOSE 8086

# Default JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"
# Default Spring profile (can be overridden by env var at runtime)
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-local}

# Recommended ENV placeholders for production DB (Neon)
# SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Dserver.port=${PORT:-8086} -jar /app/app.jar"]
