#!/bin/bash

echo "🚀 Starting Gold Trading Application in development mode..."

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Start backend in background
echo "📦 Starting backend..."
cd "$PROJECT_ROOT/backend"

# Check if mvnw exists
if [ ! -f "./mvnw" ]; then
    echo "❌ Maven wrapper not found. Please make sure mvnw exists and is executable."
    exit 1
fi

# Start the backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev &
BACKEND_PID=$!
cd "$PROJECT_ROOT"

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 15

# Start frontend
echo "🎨 Starting frontend..."
cd "$PROJECT_ROOT/frontend"

# Check if Node.js is installed
if ! command -v npm &> /dev/null; then
    echo "❌ Node.js/npm is not installed. Please install Node.js 18 or higher."
    kill $BACKEND_PID
    exit 1
fi

# Check if package.json exists
if [ ! -f "package.json" ]; then
    echo "❌ package.json not found in frontend directory."
    kill $BACKEND_PID
    exit 1
fi

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install
fi

npm run dev &
FRONTEND_PID=$!
cd "$PROJECT_ROOT"

echo "✅ Development servers started!"
echo "Frontend: http://localhost:3000"
echo "Backend: http://localhost:8080"
echo "H2 Console: http://localhost:8080/h2-console"

# Wait for user input to stop
read -p "Press [Enter] to stop development servers..."

# Kill processes
kill $BACKEND_PID $FRONTEND_PID
echo "🛑 Development servers stopped."