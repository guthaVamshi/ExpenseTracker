#!/bin/bash

# Test deployment configuration locally
echo "🧪 Testing Deployment Configuration..."

# Build the application
echo "🔨 Building application..."
./mvnw clean package -DskipTests -B

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ Build successful"

# Test with environment variables like production
echo "🚀 Testing with production-like environment variables..."

export PORT=8080
export DATABASE_URL="jdbc:postgresql://localhost:5432/postgres"
export DB_USERNAME="postgres" 
export DB_PASSWORD="0000"

# Start the application in background
echo "🟢 Starting application..."
java -Dserver.port=$PORT -Xmx512m -XX:+UseSerialGC -jar target/expense-tracker.jar &
APP_PID=$!

# Wait for startup
echo "⏳ Waiting for application to start..."
sleep 10

# Test health endpoint
echo "🩺 Testing health endpoint..."
curl -f http://localhost:8080/health

if [ $? -eq 0 ]; then
    echo "✅ Health check passed"
else
    echo "❌ Health check failed"
fi

# Test main endpoint
echo "🏠 Testing main endpoint..."
curl -f http://localhost:8080/

if [ $? -eq 0 ]; then
    echo "✅ Main endpoint accessible"
else
    echo "❌ Main endpoint failed"
fi

# Stop the application
echo "🛑 Stopping application..."
kill $APP_PID

echo "🎉 Test completed"
