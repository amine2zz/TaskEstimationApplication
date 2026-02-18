# ⚙️ Proxym Backend (Spring Boot)

The core engine of the Proxym Recommendation platform, handling data persistence, business logic, and security with a high-performance, sustainable architecture.

## 🏗️ Architecture: Clean & Sustainable
The backend follows the **Clean Architecture** pattern to ensure long-term maintainability:
- **DTO Layer**: Decouples external API contracts from internal data entities.
- **Service Interfaces**: Abstracted business logic allowing multiple implementations.
- **Service Impl Layer**: Decomposed, single-responsibility business logic implementations.
- **Security Logic**: BCrypt hashing and secure DTO mapping to protect sensitive data.

## 📦 API Endpoints
- `/api/auth`: Login and Signup flow (Secure BCrypt storage).
- `/api/users`: Profile and administrative management via `UserDTO`.
- `/api/transactions`: Financial activity logging via `TransactionDTO`.
- `/api/products`: Financial product catalog via `FinancialProductDTO`.
- `/api/recommendations`: Interface to the AI module, returning personalized `FinancialProductDTO` lists.

## 🛠️ Tech Stack
- **Java 17 / Spring Boot 3.4**: Core platform.
- **Spring Security**: Access control and password hashing.
- **Spring Data JPA / PostgreSQL**: Persistence layer.
- **Lombok**: Boilerplate reduction.

## 🚀 Execution
Uses the Maven Daemon (`mvnd`) for optimized build performance:
```powershell
.\start.bat (from root)
```

## 📋 Data Seeding
Automated seeding via `DataInitializer` using the Service layer to ensure consistency and secure password creation:
- 1 Admin User (`admin@proxym.com` / `admin`)
- Various mock users with 6 months of generated transaction history.
