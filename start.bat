@echo off
TITLE Proxym Recommendation Engine - Full Stack Startup

echo ======================================================
echo 🚀 STARTING PROXYM RECOMMENDATION ENGINE
echo ======================================================
echo.

:: 1. Start AI Module (Python FastAPI)
echo [1/3] Starting AI Module on Port 8005...
start "AI_MODULE" cmd /k "cd ai_module && ..\.venv\Scripts\python main.py"

:: 2. Start Backend (Spring Boot)
echo [2/3] Starting Java Backend on Port 8081...
start "BACKEND" cmd /k "cd backend && ^& 'C:\Users\ghari\OneDrive\Bureau\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64\bin\mvnd.exe' spring-boot:run"

:: 3. Start Frontend (React + Vite)
echo [3/3] Starting React Frontend on Port 3000...
start "FRONTEND" cmd /k "cd frontend && npm run dev -- --port 3000"

echo.
echo ======================================================
echo ✅ ALL SERVICES INITIATED
echo.
echo 🌐 Dashboard: http://localhost:3000
echo ⚙️ Backend API: http://localhost:8081
echo 🤖 AI Module: http://localhost:8005
echo ======================================================
echo.
echo Close the individual terminal windows to stop services.
pause
