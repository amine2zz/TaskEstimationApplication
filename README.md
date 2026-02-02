# Intelligent Financial Product Recommendation Engine (PRX-2026-15)

## 🌟 Project Overview
This project is a high-performance, AI-driven banking module designed to recommend financial products (Savings, Investments, Loans, Insurance) based on deep analysis of user spending habits and risk profiles. 

It features a **Premium Apple-inspired UI** and a hybrid backend architecture combining **Spring Boot** for transaction management and **FastAPI** for real-time ML processing.

---

## 🏗️ Architecture
- **Frontend**: React JS + Vite (Port 3000)
- **Backend (Core)**: Spring Boot (Port 8081)
- **AI Module**: Python FastAPI (Port 8005)
- **Database**: PostgreSQL 17

---

## ⚙️ Prerequisites
- **Java**: 17.0.12
- **Node.js**: v25.4.0
- **Python**: 3.14.2
- **Maven Daemon (mvnd)**: Located in Desktop folder
- **PostgreSQL**: Version 17 (Running on default port 5432)

---

## 🚀 Getting Started

### 1. Database Initialization
1. Open **pgAdmin 4**.
2. Create a database named `proxym_recommendation`.
3. Connectivity Check:
   - **User**: `postgres`
   - **Password**: `4175` (Configured in `application.properties`)

### 2. Start the AI Module (ML API)
```powershell
cd ai_module
# Activate environment
..\.venv\Scripts\Activate.ps1
# Launch Service
python main.py
```
*Live at:* [http://localhost:8005](http://localhost:8005)

### 3. Start the Backend (Core API)
```powershell
cd backend
# Build and Run with Maven Daemon
& "C:\Users\ghari\OneDrive\Bureau\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64\bin\mvnd.exe" spring-boot:run
```
*Live at:* [http://localhost:8081](http://localhost:8081)

### 4. Start the Frontend (User Dashboard)
```powershell
cd frontend
# Install dependencies
npm install
# Start dev server on specific port
npm run dev -- --port 3000
```
*Live at:* [http://localhost:3000](http://localhost:3000)

---

## 🛠️ Current Status & Implemented Features
- [x] **Database Schema**: Unified models for `User`, `Transaction`, and `FinancialProduct`.
- [x] **AI Engine**: FastAPI implementation with Spending & Risk Ratio logic.
- [x] **Auto-Port Conflict Resolution**: Configured custom ports (8081, 8005, 3000) to avoid common port conflicts.
- [x] **Design System**: Premium CSS baseline with glassmorphism effects.

## 📁 Project Structure
- `/ai_module`: Python-based AI recommendation logic.
- `/backend`: Java Spring Boot enterprise backend.
- `/frontend`: Responsive React dashboard.
- `/.venv`: Pre-configured Python virtual environment.
