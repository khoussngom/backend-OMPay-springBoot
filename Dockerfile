# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Build arguments
ARG JAR_FILE=target/UsersMicroservice-0.0.1-SNAPSHOT.jar

# Copy jar from build context
COPY ${JAR_FILE} app.jar

# Expose Render PORT (Render sets PORT env var at runtime)
EXPOSE 8080 8086

# Environment (can be overridden by Render env vars)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

