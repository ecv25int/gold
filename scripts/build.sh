#!/bin/bash

echo "🏗️  Building Gold Trading Application..."

# Build backend
echo "📦 Building backend..."
cd backend
./mvnw clean package -DskipTests
cd ..

# Build frontend
echo "🎨 Building frontend..."
cd frontend
npm install
npm run build
cd ..

echo "✅ Build completed successfully!"

# Run with Docker
echo "🐳 Starting with Docker..."
docker-compose up --build -d

echo "🚀 Application is running!"
echo "Frontend: http://localhost:3000"
echo "Backend: http://localhost:8080"
echo "API Docs: http://localhost:8080/swagger-ui.html"