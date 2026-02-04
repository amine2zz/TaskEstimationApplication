@echo off
SETLOCAL EnableDelayedExpansion
TITLE Proxym Recommendation Engine - Full Stack Startup

echo ======================================================
echo 🚀 STARTING PROXYM RECOMMENDATION ENGINE
echo ======================================================
echo.

:: --- PORT CLEANUP ---
echo [0/3] Cleaning up ports...
for %%p in (8081 8005 3000) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%p ^| findstr LISTENING') do (
        echo killing process %%a on port %%p...
        taskkill /F /PID %%a >nul 2>&1
    )
)

:: --- DEPENDENCY CHECKS ---
where java >nul 2>&1 || (echo ERROR: Java not found! && pause && exit)
where node >nul 2>&1 || (echo ERROR: Node.js not found! && pause && exit)

:: --- SERVICE STARTUP ---

:: 1. Start AI Module (Python FastAPI)
echo [1/3] Starting AI Module on Port 8005...
start "AI_MODULE" cmd /k "cd ai_module && ..\.venv\Scripts\python main.py"

:: 2. Start Backend (Spring Boot)
echo [2/3] Starting Java Backend on Port 8081...
:: Using the dedicated mvnd for performance
set MVND_PATH="C:\Users\ghari\OneDrive\Bureau\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64\bin\mvnd.exe"
start "BACKEND" cmd /k "cd backend && !MVND_PATH! spring-boot:run"

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
echo NOTE: Initial backend startup may take 10-20 seconds.
echo Keep these windows open while using the app.
pause
