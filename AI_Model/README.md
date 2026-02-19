# 🧠 Proxym Strategic AI System

This module contains the "Intelligence Unit" of the Proxym Platform. It has been transformed from simple rule-based logic into a **shrewd strategic brain** trained on millions of real banking data points.

## 📂 Final Clean Structure
- `financial_recommender.pkl`: The core Gradient Boosting model (High Accuracy).
- `data_scaler.pkl`: Ensures new data is normalized before processing.
- `master_train.py`: The single source of truth for retraining the model.
- `test_ai.py`: CLI testing utility.
- `test_interface.html`: **[NEW]** A visual dashboard to test the AI in real-time.

## 🛠️ What was Developed?
1. **Multi-Source Data Integration**: We integrated 4 major banking datasets covering churn, customer profiling, and 1M+ transactions.
2. **Behavioral Benchmarking**: The AI doesn't just look at a balance; it compares the user against global bank averages to identify "High Spenders" or "Savers."
3. **Strategic Management Logic**:
   - **Investment Logic**: Pivots to luxury growth products for active, high-net-worth clients.
   - **Loan Logic**: Identifies credit-hungry profiles with stable income gaps.
   - **Savings Logic**: Optimizes for security and liquidity for middle-class demographics.
4. **Chat Interface Foundation**: An asynchronous `/chat` endpoint is ready for integration into a frontend chatbot.

## 🚀 Testing the Model
1. Start the **AI_Module** via `start.bat`.
2. Open `AI_Model/test_interface.html` in your browser.
3. Input custom demographics and witness the strategic recommendation in real-time.

---
*Ensuring privacy and compliance: All model artifacts are stored locally and process data in-memory without persistent logs.*
