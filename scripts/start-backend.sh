#!/bin/bash

echo "🚀 Starting Gold Trading Backend..."

cd /Users/C58629A/Documents/ProjectsJHipster/gold-app/backend

# Check if Maven is installed
if command -v mvn &> /dev/null; then
    echo "📦 Using system Maven..."
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
elif [ -f "./mvnw" ]; then
    echo "📦 Using Maven wrapper..."
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
else
    echo "❌ Neither Maven nor Maven wrapper found."
    echo "Please install Maven or ensure mvnw exists and is executable."
    exit 1
fi