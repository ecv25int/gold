#!/bin/bash

echo "🚀 Starting Gold Trading Application in development mode..."

# Check if Maven is installed
if command -v mvn &> /dev/null; then
    echo "📦 Using system Maven..."
    MAVEN_CMD="mvn"
elif [ -f "./backend/mvnw" ]; then
    echo "📦 Using Maven wrapper..."
    MAVEN_CMD="./mvnw"
else
    echo "❌ Neither Maven nor Maven wrapper found. Please install Maven or ensure mvnw is executable."
    exit 1
fi

# Start backend
echo "📦 Starting backend with $MAVEN_CMD..."
cd backend
$MAVEN_CMD spring-boot:run -Dspring-boot.run.profiles=dev &
BACKEND_PID=$!
cd ..

echo "⏳ Waiting for backend to start..."
sleep 15

# Start frontend
echo "🎨 Starting frontend..."
cd frontend

# Check if Node.js is installed
if ! command -v npm &> /dev/null; then
    echo "❌ Node.js/npm is not installed. Please install Node.js 18 or higher."
    kill $BACKEND_PID
    exit 1
fi

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install
fi

npm run dev &
FRONTEND_PID=$!
cd ..

echo "✅ Development servers started!"
echo ""
echo "📱 Frontend: http://localhost:3000"
echo "🔧 Backend API: http://localhost:8080"
echo "💾 H2 Console: http://localhost:8080/h2-console"
echo "📚 API Docs: http://localhost:8080/swagger-ui.html"
echo ""
echo "Default login credentials:"
echo "  👤 Demo User: demo / demo123"
echo "  🔐 Admin User: admin / admin123"
echo ""

# Wait for user input to stop
read -p "Press [Enter] to stop development servers..."

# Kill processes
echo "🛑 Stopping development servers..."
kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
echo "✅ Development servers stopped."