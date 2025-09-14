#!/bin/bash

# Render Startup Script for Expense Tracker
echo "Starting Expense Tracker with optimized JVM settings..."

# Set Java options for Render environment
export JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Set Spring profile
export SPRING_PROFILES_ACTIVE=prod

# Health check URL for monitoring
export HEALTH_CHECK_URL="http://localhost:${PORT:-8080}/health"

# Start the application
echo "Java options: $JAVA_OPTS"
echo "Active profile: $SPRING_PROFILES_ACTIVE"
echo "Port: ${PORT:-8080}"

java $JAVA_OPTS -jar target/expense-tracker.jar

echo "Application started. Health check available at: $HEALTH_CHECK_URL"
