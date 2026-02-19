import pandas as pd
import joblib
import os
import numpy as np
import faiss
from sentence_transformers import SentenceTransformer
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional

# --- PATH CONFIGURATION ---
DIR_PATH = os.path.dirname(os.path.realpath(__file__))
AI_MODEL_DIR = os.path.join(DIR_PATH, "..", "AI_Model")

app = FastAPI(title="Proxym Smart Banking API")

# Enable CORS for frontend/testing interface
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- GLOBAL MODEL LOADING ---
try:
    print("🧠 Loading Strategic Brain...")
    strategic_model = joblib.load(os.path.join(AI_MODEL_DIR, "financial_recommender.pkl"))
    data_scaler = joblib.load(os.path.join(AI_MODEL_DIR, "data_scaler.pkl"))
    
    print("🔍 Loading Knowledge Base (RAG)...")
    kb_model = SentenceTransformer('all-MiniLM-L6-v2')
    kb_index = faiss.read_index(os.path.join(AI_MODEL_DIR, "kb_index.faiss"))
    kb_data = joblib.load(os.path.join(AI_MODEL_DIR, "kb_metadata.pkl"))
    print("✅ AI Ecosystem Loaded Successfully.")
except Exception as e:
    print(f"⚠️ Warning: Could not load all AI components: {e}")

# --- SCHEMAS ---
class UserFinancials(BaseModel):
    credit_score: int
    age: int
    tenure: int
    balance: float
    num_products: int
    has_crcard: int
    is_active: int
    salary: float
    satisfaction: int

class ChatMessage(BaseModel):
    message: str
    user_context: Optional[dict] = None

# --- ENDPOINTS ---

@app.get("/")
def health_check():
    return {"status": "AI Banking Engine Online", "version": "2.0-Strategic"}

@app.post("/recommend")
def recommend_from_profile(user: UserFinancials):
    try:
        features = pd.DataFrame([[
            user.credit_score, user.age, user.tenure, user.balance,
            user.num_products, user.has_crcard, user.is_active,
            user.salary, user.satisfaction
        ]], columns=['CreditScore', 'Age', 'Tenure', 'Balance', 'NumOfProducts', 
                    'HasCrCard', 'IsActiveMember', 'EstimatedSalary', 'Satisfaction Score'])
        
        features_scaled = data_scaler.transform(features)
        prediction = strategic_model.predict(features_scaled)[0]
        
        return {
            "prediction": prediction,
            "confidence": float(np.max(strategic_model.predict_proba(features_scaled)[0]))
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Recommendation Engine Error: {str(e)}")

@app.post("/chat")
def intelligent_chat(request: ChatMessage):
    try:
        query = request.message.strip()
        context = request.user_context or {}
        msg_low = query.lower()
        
        # User dynamic data
        user_name = context.get('name', 'amine')
        balance = context.get('balance', 0)
        salary = context.get('salary', 3000)
        risk = context.get('risk_profile', 'Medium')
        
        # --- PROACTIVE INTELLIGENCE: Transaction Analysis ---
        txs = context.get('transactions', [])
        total_spent = sum(t.get('amount', 0) for t in txs if t.get('amount', 0) > 0)
        
        # --- HYBRID ROUTING ---
        
        # 1. Identity & Profile
        if any(w in msg_low for w in ["who am i", "my name", "profile", "identity"]):
            return {"response": f"You are {user_name}, a valued Proxym member. Your account shows a {risk} risk profile with a current balance of ${balance:,.2f}."}

        # 2. Account Health (Balance & Spending)
        if any(w in msg_low for w in ["balance", "money", "much i have", "funds"]):
            status = "healthy" if balance > 1000 else "critical" if balance < 0 else "low"
            advice = "You might want to check your recent overshoots." if status == "critical" else "You're in a great position to invest!"
            return {"response": f"Your current status is {status.upper()} (${balance:,.2f}). {advice}"}

        if any(w in msg_low for w in ["spend", "analyze", "history", "transactions", "buying"]):
            if not txs:
                return {"response": "I see no recent transactions to analyze. Start by adding one in your dashboard!"}
            return {"response": f"Analysis Complete: You've had {len(txs)} interactions recently, totaling ${total_spent:,.2f}. Your balance is currently ${balance:,.2f}. Recommended: Reduce Entertainment spending to improve your health score."}

        # 3. Strategy & Expert Advice (RAG + Strategic Model Intelligence)
        if any(w in msg_low for w in ["advice", "invest", "suggest", "help", "loan", "how can i"]):
            # Use the index we built from 100,000 real banking patterns
            query_vector = kb_model.encode([query])
            distances, indices = kb_index.search(np.array(query_vector).astype('float32'), k=1)
            ctx = kb_data[indices[0][0]]
            
            # Smart logic based on balance
            if balance < 0:
                return {"response": f"Before investing, we should address your negative balance. Strategic Tip: {ctx}. Proxym suggests a micro-loan or balance transfer."}
            return {"response": f"PROXIM STRATEGY: {ctx}. For your ${salary:,.2f} income level, this is the optimal move."}

        # 4. Smart Multi-Response (Fallback)
        return {
            "response": f"Hello {user_name}! I'm Proxym AI. I've analyzed your ${balance:,.2f} balance and recent activity. Try asking me:\n"
                        "• 'Analyze my transaction history'\n"
                        "• 'How much did I spend recently?'\n"
                        "• 'Give me a strategic investment tip'"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI Brain Error: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8005)
