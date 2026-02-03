# ⚙️ Proxym Backend (Spring Boot)

The core engine of the Proxym Recommendation platform, handling data persistence, business logic, and security.

## 🛠️ Tech Stack
- **Java 17**: Core language.
- **Spring Boot 3.4**: Application framework.
- **Spring Data JPA**: Database orchestration.
- **PostgreSQL**: Robust persistence layer.
- **Hibernate**: ORM for complex data mapping.

## 📦 Key APIs
- `/api/auth`: Login and Signup flow.
- `/api/users`: Profile and account management.
- `/api/transactions`: Financial activity logging.
- `/api/products`: Financial product catalog.
- `/api/recommendations`: Interface to the AI module.

## 🚀 Execution
Uses the Maven Daemon (`mvnd`) for optimized build performance:
```powershell
& 'C:\Users\ghari\OneDrive\Bureau\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64\bin\mvnd.exe' spring-boot:run
```

## 📋 Data Seeding
On first run, the system automatically seeds the database with:
- 1 Admin User (`admin@proxym.com`)
- Mock Users (John, Sarah)
- Initial Financial Products & Transaction history.
