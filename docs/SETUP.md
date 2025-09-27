# Gold Trading Application - Setup Guide

## Prerequisites

- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 14+** (for production)
- **Docker & Docker Compose** (optional, for containerized deployment)

## Quick Start

### Option 1: Development Mode (Recommended for development)

1. **Clone and navigate to the project:**
```bash
cd /Users/C58629A/Documents/ProjectsJHipster/gold-app
```

2. **Start development servers:**
```bash
./scripts/dev.sh
```

This will:
- Start the backend with H2 in-memory database
- Start the frontend development server
- Open H2 console at http://localhost:8080/h2-console

**Access URLs:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- H2 Database Console: http://localhost:8080/h2-console
- API Documentation: http://localhost:8080/swagger-ui.html

### Option 2: Docker Deployment

1. **Build and run with Docker:**
```bash
./scripts/build.sh
```

This will:
- Build both frontend and backend
- Start PostgreSQL database
- Deploy the full application stack

**Access URLs:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

## Manual Setup

### Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Install dependencies and run:**
```bash
./mvnw clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Setup

1. **Navigate to frontend directory:**
```bash
cd frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Start development server:**
```bash
npm run dev
```

## Database Setup

### For Development (H2)
The application uses H2 in-memory database by default in development mode. No setup required.

### For Production (PostgreSQL)

1. **Create database and user:**
```sql
CREATE DATABASE goldtradingdb;
CREATE USER golduser WITH PASSWORD 'goldpass';
GRANT ALL PRIVILEGES ON DATABASE goldtradingdb TO golduser;
```

2. **Run the schema script:**
```bash
psql -U golduser -d goldtradingdb -f backend/src/main/resources/schema.sql
```

## Default Users

The application comes with pre-configured demo users:

### Admin User
- **Username:** admin
- **Password:** admin123
- **Balance:** $10,000
- **Role:** ADMIN

### Demo User
- **Username:** demo
- **Password:** demo123
- **Balance:** $5,000
- **Gold Holdings:** 2.5 oz
- **Role:** USER

## Features

- ✅ User authentication and registration
- ✅ Real-time gold price display
- ✅ Buy/sell gold transactions
- ✅ Transaction history
- ✅ Portfolio management
- ✅ Admin controls
- ✅ Responsive web interface
- ✅ RESTful API
- ✅ Docker deployment
- ✅ CI/CD pipeline ready

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React SPA     │    │  Spring Boot    │    │   PostgreSQL    │
│   (Frontend)    │◄──►│   (Backend)     │◄──►│   (Database)    │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Technology Stack

### Backend
- Spring Boot 3.1.5
- Spring Security
- Spring Data JPA
- PostgreSQL / H2
- JWT Authentication
- Maven

### Frontend
- React 18
- TypeScript
- Vite
- Tailwind CSS
- React Query
- React Router

### DevOps
- Docker & Docker Compose
- GitHub Actions
- Nginx

## Troubleshooting

### Port Already in Use
If ports 3000 or 8080 are already in use:
```bash
# Kill processes on these ports
lsof -ti:3000 | xargs kill
lsof -ti:8080 | xargs kill
```

### Database Connection Issues
Check PostgreSQL is running:
```bash
# macOS with Homebrew
brew services start postgresql
```

### Permission Issues with Scripts
Make scripts executable:
```bash
chmod +x scripts/*.sh
```