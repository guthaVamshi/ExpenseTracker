#!/bin/bash

# 🚀 Quick Deployment Script for Expense Tracker

echo "🚀 Starting deployment preparation..."

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "📝 Initializing git repository..."
    git init
    git add .
    git commit -m "Initial commit for deployment"
    echo "✅ Git repository initialized"
    echo "👉 Now push this to GitHub and continue with the deployment guide"
else
    echo "✅ Git repository already exists"
fi

# Build the backend
echo "🔨 Building backend..."
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Backend build successful"
else
    echo "❌ Backend build failed"
    exit 1
fi

# Build the frontend
echo "🔨 Building frontend..."
cd frontend
npm install
npm run build

if [ $? -eq 0 ]; then
    echo "✅ Frontend build successful"
    cd ..
else
    echo "❌ Frontend build failed"
    exit 1
fi

echo ""
echo "🎉 Build completed successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Push your code to GitHub if you haven't already"
echo "2. Follow the DEPLOYMENT_GUIDE.md for detailed instructions"
echo "3. Set up Neon PostgreSQL database"
echo "4. Deploy backend to Railway"
echo "5. Deploy frontend to Netlify"
echo ""
echo "💡 Tip: Read DEPLOYMENT_GUIDE.md for step-by-step instructions"
