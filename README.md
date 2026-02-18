# 🚀 Proxym Recommendation Engine - Quick Start

This project is an AI-driven financial recommendation system built with a **sustainable, enterprise-grade architecture** designed for scalability and high maintainability.

## 📂 Project Structure
- `/frontend`: React + Vite dashboard with Apple-inspired design and glassmorphism.
- `/backend`: Java Spring Boot core API using **Clean Architecture** (DTOs, Interfaces, Service Impls).
- `/ai_module`: Python FastAPI microservice providing specialized ML-based recommendations.

## 🏗️ Architectural Excellence (Sustainable & Scalable)
- **DTO Pattern**: Decoupled API layer from database models for zero-breakage updates.
- **Interface-Driven Design**: Pluggable business logic through Spring Interfaces.
- **Microservice Ready**: Pluggable AI module with configurable endpoint URLs.
- **Deep Decomposition**: Every logical unit is split into small, single-responsibility functions.

## 🔐 Advanced Security
- **BCrypt Hashing**: Industry-standard secure password storage (no plain-text passwords).
- **Data Protection**: Automatic filtering of sensitive data (like password hashes) from all API responses via DTOs.
- **Spring Security**: Integrated security framework for robust access control.

## ⚡ Unified Startup
To start all services at once, use the `start.bat` file in the root directory:
```powershell
.\start.bat
```

## 🌐 Dashboard Access
- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: [http://localhost:8081](http://localhost:8081)
- **AI Module**: [http://localhost:8005](http://localhost:8005)

---

## 🛠️ Technical Stack
- **Languages**: Java 17, Python 3.12+, JavaScript (React 19)
- **Frameworks**: Spring Boot 3.4, FastAPI, Vite, Spring Security
- **Database**: PostgreSQL 17 (Agnostic layer, easily swappable)
- **Design**: Vanilla CSS, Framer Motion, Lucide Icons

## 🛠️ Management & Admin
Access the Admin Panel at `/admin` to manage:
- **Products**: Create and edit financial products.
- **Users**: Manage accounts and secure profiles.
- **Transactions**: Monitor and log financial activity.

Default Admin: `admin@proxym.com` / `admin`
