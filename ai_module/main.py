from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional
import random

app = FastAPI()

class UserData(BaseModel):
    user_id: int
    spending_habits: dict
    risk_level: str
    balance: float
    monthly_income: float

class Recommendation(BaseModel):
    product_name: str
    confidence: float
    reason: str
    type: str # SAVINGS, INVESTMENT, LOAN

@app.post("/recommend", response_model=List[Recommendation])
async def get_recommendations(data: UserData):
    recommendations = []
    
    # 1. High Investment Ratio Logic
    inv_ratio = data.spending_habits.get("investment_ratio", 0)
    
    if inv_ratio > 0.25:
        recommendations.append(Recommendation(
            product_name="Global Tech ETF",
            confidence=0.95,
            reason=f"Based on your high investment ratio ({inv_ratio:.1%}), you could benefit from diversified tech exposure.",
            type="INVESTMENT"
        ))
    elif inv_ratio > 0.1:
         recommendations.append(Recommendation(
            product_name="Real Estate REIT",
            confidence=0.88,
            reason="You have steady investment habits. Diversifying into Real Estate could stabilize your portfolio.",
            type="INVESTMENT"
        ))

    # 2. Risk Level Logic
    if data.risk_level == "High":
        recommendations.append(Recommendation(
            product_name="Luxury Growth Portfolio",
            confidence=0.91,
            reason="Your appetite for high risk aligns perfectly with our aggressive growth portfolio.",
            type="INVESTMENT"
        ))
    elif data.risk_level == "Medium":
        recommendations.append(Recommendation(
            product_name="Secure Yield Savings",
            confidence=0.85,
            reason="Balancing growth with security: This account offers protected high-yield returns.",
            type="SAVINGS"
        ))
    else: # Low Risk
        recommendations.append(Recommendation(
            product_name="Premium Plus Savings",
            confidence=0.98,
            reason="Maximize your liquid capital with our safest high-yield option, ideal for low-risk tolerance.",
            type="SAVINGS"
        ))

    # 3. Liquidity/Balance Logic
    if data.balance > 20000:
        recommendations.append(Recommendation(
            product_name="Premium Plus Savings",
            confidence=0.82,
            reason="Holding substantial idle cash. Move funds here for 5.2% APY with instant liquidity.",
            type="SAVINGS"
        ))
        
    # 4. Debt/Income Logic (Simulated Loan need)
    if data.balance < 1000 and data.monthly_income > 4000:
        recommendations.append(Recommendation(
            product_name="Flexi-Loan Silver",
            confidence=0.75,
            reason="Low liquidity detected despite high income. Use our Flexi-Loan for immediate bridging needs.",
            type="LOAN"
        ))

    # Shuffle and limit to top 3
    random.shuffle(recommendations)
    return recommendations[:3]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8005)
