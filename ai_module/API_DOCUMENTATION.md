# 🚀 Proxym Intelligence API (Python/FastAPI)

The AI Module has been fully transformed into a **RESTful Gateway** for the Strategic Brain and the Intelligent Chatbot.

## 📡 API Endpoints

### 1. `POST /recommend`
**Purpose**: High-precision financial product targeting.
- **Input**: User demographics, balance, salary, and behavioral scores (CreditScore, ActiveMember, etc.).
- **Output**: The most suitable product category (`SAVINGS`, `INVESTMENT`, `LOAN`, `INSURANCE`) with a confidence score.

### 2. `POST /chat`
**Purpose**: RAG-powered (Retrieval Augmented Generation) Intelligent Chatbot.
- **Input**: Natural language message.
- **How it works**:
  - The AI takes your message and generates a "semantic vector."
  - It searches the **FAISS Knowledge Base** (built from millions of banking records).
  - It retrieves the most relevant financial context to provide an intelligent, data-backed answer.

### 3. `GET /`
**Purpose**: System health check.

## 🛠️ Technology Stack
- **FastAPI**: Asynchronous, high-performance API framework.
- **Scikit-Learn**: Powering the strategic recommendation engine.
- **Sentence-Transformers**: Turning banking knowledge into math (embeddings).
- **FAISS (Facebook AI Similarity Search)**: Sub-millisecond retrieval of banking context.

## 🚀 How to Call the API
The API runs on **Port 8005** by default.

**Sample Python Call:**
```python
import requests
res = requests.post("http://localhost:8005/recommend", json={
    "credit_score": 750, "age": 35, "tenure": 5, "balance": 60000.0,
    "num_products": 2, "has_crcard": 1, "is_active": 1, "salary": 95000.0, "satisfaction": 4
})
print(res.json())
```

---
*The API is now fully synchronized with the Java Backend's RecommendationService.*
