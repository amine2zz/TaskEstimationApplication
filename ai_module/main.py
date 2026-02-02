from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import random

app = FastAPI()

class UserData(BaseModel):
    user_id: int
    spending_habits: dict
    risk_level: str

class Recommendation(BaseModel):
    product_name: str
    confidence: float
    reason: str

@app.post("/recommend", response_model=List[Recommendation])
async def get_recommendations(data: UserData):
    # Simulated ML Logic
    # In a real scenario, this would load a pre-trained scikit-learn or TensorFlow model
    
    recommendations = []
    
    if data.spending_habits.get("investment_ratio", 0) > 0.2:
        recommendations.append(Recommendation(
            product_name="Luxury Growth Portfolio",
            confidence=0.92,
            reason="High investment ratio detected in transaction history."
        ))
    
    if data.risk_level == "Low":
        recommendations.append(Recommendation(
            product_name="Secure Yield Savings",
            confidence=0.85,
            reason="Aligns with your conservative risk profile."
        ))
    
    return recommendations

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8005)
