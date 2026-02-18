# 🤖 Proxym AI Module (FastAPI)

A specialized microservice providing financial intelligence and personalized product recommendations using a granular, rule-based inference engine.

## 🏗️ Intelligence Units
The recommendation engine is decomposed into specialized logical units:
- **Spending Unit**: Analyzes investment-to-spending ratios to suggest growth assets.
- **Risk Unit**: Maps user risk profiles (High/Med/Low) to curated portfolios.
- **Liquidity Unit**: Detects idle cash or bridging needs to suggest savings or loan products.

## 🛠️ Tech Stack
- **Python 3.12+**: High-performance backend logic.
- **FastAPI**: Modern, asynchronous web framework.
- **Pydantic**: Type-safe data validation.
- **Uvicorn**: ASGI server implementation.

## 🚀 Execution
```bash
uvicorn main:app --host 0.0.0.0 --port 8005
```
*Accessible on port 8005.*
