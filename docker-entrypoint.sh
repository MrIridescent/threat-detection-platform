#!/bin/bash
set -e

# Print environment info for debugging
echo "Starting Threat Detection Platform"
echo "Java version: $(java -version 2>&1 | head -n 1)"
echo "Active profiles: $SPRING_PROFILES_ACTIVE"

# Configure JVM memory settings if not specified
if [ -z "$JAVA_OPTS" ]; then
  export JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
  echo "Using default JAVA_OPTS: $JAVA_OPTS"
fi

# Wait for dependent services
wait_for() {
    echo "Waiting for $1 to be available..."
    local host="$(echo $1 | cut -d: -f1)"
    local port="$(echo $1 | cut -d: -f2)"
    local count=0
    local max=60
    while ! nc -z "$host" "$port" > /dev/null 2>&1; do
        count=$((count+1))
        if [ $count -gt $max ]; then
            echo "Error: Timed out waiting for $1 to become available"
            exit 1
        fi
        echo "Waiting for $1 to be available... ${count}/${max}"
        sleep 1
    done
    echo "$1 is available."
}

# Extract database host and port from JDBC URL
if [ ! -z "$SPRING_DATASOURCE_URL" ]; then
    DB_HOST=$(echo $SPRING_DATASOURCE_URL | sed 's/.*\/\/\([^:]*\):.*/\1/')
    DB_PORT=$(echo $SPRING_DATASOURCE_URL | sed 's/.*:\([0-9]*\)\/.*/\1/')

    if [ ! -z "$DB_HOST" ] && [ ! -z "$DB_PORT" ]; then
        wait_for "$DB_HOST:$DB_PORT"
    fi
fi

# Wait for Redis if configured
if [ ! -z "$SPRING_REDIS_HOST" ] && [ ! -z "$SPRING_REDIS_PORT" ]; then
    wait_for "$SPRING_REDIS_HOST:$SPRING_REDIS_PORT"
fi

# Start the application
exec java $JAVA_OPTS -jar /app/app.jar "$@"
