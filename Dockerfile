# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the Maven configuration files
COPY pom.xml .

# Download all required dependencies
RUN mvn dependency:go-offline -B

# Copy the project source
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install dependencies and security updates
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y curl jq && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user to run the application
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set up application directory
RUN mkdir -p /app/logs && \
    chown -R appuser:appuser /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Copy startup scripts
COPY docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Set health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set the entrypoint
ENTRYPOINT ["/app/docker-entrypoint.sh"]
