from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import random
import logging

# Configure logging to track AI operations
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Recommendation AI Module",
    description="Microservice providing financial intelligence for product recommendations."
)

class UserData(BaseModel):
    """Input schema for user financial data."""
    user_id: int
    spending_habits: Dict[str, Any]
    risk_level: str
    balance: float
    monthly_income: float

class Recommendation(BaseModel):
    """Output schema for a single financial product recommendation."""
    product_name: str
    confidence: float
    reason: str
    type: str # SAVINGS, INVESTMENT, LOAN

# --- Spending Logic Decomposition ---

def create_tech_etf_recommendation(ratio: float) -> Recommendation:
    """Creates a high-growth tech ETF recommendation."""
    return Recommendation(
        product_name="Global Tech ETF",
        confidence=0.95,
        reason=f"Based on your high investment ratio ({ratio:.1%}), you could benefit from diversified tech exposure.",
        type="INVESTMENT"
    )

def create_reit_recommendation() -> Recommendation:
    """Creates a real estate investment recommendation."""
    return Recommendation(
        product_name="Real Estate REIT",
        confidence=0.88,
        reason="You have steady investment habits. Diversifying into Real Estate could stabilize your portfolio.",
        type="INVESTMENT"
    )

def get_spending_recommendations(inv_ratio: float) -> List[Recommendation]:
    """Evaluates spending habits to suggest investment products."""
    recs = []
    if is_high_investment(inv_ratio):
        recs.append(create_tech_etf_recommendation(inv_ratio))
    elif is_medium_investment(inv_ratio):
        recs.append(create_reit_recommendation())
    return recs

def is_high_investment(ratio: float) -> bool:
    """Predicate for high investment behavior."""
    return ratio > 0.25

def is_medium_investment(ratio: float) -> bool:
    """Predicate for medium investment behavior."""
    return ratio > 0.1

# --- Risk Logic Decomposition ---

def get_high_risk_product() -> Recommendation:
    """Product for aggressive risk profiles."""
    return Recommendation(
        product_name="Luxury Growth Portfolio",
        confidence=0.91,
        reason="Your appetite for high risk aligns perfectly with our aggressive growth portfolio.",
        type="INVESTMENT"
    )

def get_medium_risk_product() -> Recommendation:
    """Product for balanced risk profiles."""
    return Recommendation(
        product_name="Secure Yield Savings",
        confidence=0.85,
        reason="Balancing growth with security: This account offers protected high-yield returns.",
        type="SAVINGS"
    )

def get_low_risk_product() -> Recommendation:
    """Product for conservative risk profiles."""
    return Recommendation(
        product_name="Premium Plus Savings",
        confidence=0.98,
        reason="Maximize your liquid capital with our safest high-yield option, ideal for low-risk tolerance.",
        type="SAVINGS"
    )

def get_risk_recommendations(risk_level: str) -> List[Recommendation]:
    """Maps risk level to specific financial products."""
    risk_map = {
        "High": get_high_risk_product,
        "Medium": get_medium_risk_product,
        "Low": get_low_risk_product
    }
    # Default to medium if unknown
    generator = risk_map.get(risk_level, get_medium_risk_product)
    return [generator()]

# --- Liquidity Logic Decomposition ---

def get_high_balance_product() -> Recommendation:
    """Product for users with significant idle cash."""
    return Recommendation(
        product_name="Premium Plus Savings",
        confidence=0.82,
        reason="Holding substantial idle cash. Move funds here for 5.2% APY with instant liquidity.",
        type="SAVINGS"
    )

def get_bridge_loan_product() -> Recommendation:
    """Product for high-income users with low liquidity."""
    return Recommendation(
        product_name="Flexi-Loan Silver",
        confidence=0.75,
        reason="Low liquidity detected despite high income. Use our Flexi-Loan for immediate bridging needs.",
        type="LOAN"
    )

def get_liquidity_recommendations(balance: float, income: float) -> List[Recommendation]:
    """Analyzes cash flow and balance to suggest liquidity solutions."""
    recs = []
    if has_high_idle_cash(balance):
        recs.append(get_high_balance_product())
    
    if needs_bridging_loan(balance, income):
        recs.append(get_bridge_loan_product())
    return recs

def has_high_idle_cash(balance: float) -> bool:
    """Predicate for high liquidity surplus."""
    return balance > 20000

def needs_bridging_loan(balance: float, income: float) -> bool:
    """Predicate for potential loan need."""
    return balance < 1000 and income > 4000

# --- Main Endpoint ---

@app.post("/recommend", response_model=List[Recommendation])
async def get_recommendations(data: UserData):
    """
    Main entry point for AI recommendations.
    
    Aggregates logic from spending habits, risk profiles, and liquidity status
    to provide the top 3 personalized financial products.
    """
    logger.info(f"Processing recommendations for user {data.user_id}")
    
    try:
        inv_ratio = data.spending_habits.get("investment_ratio", 0)
        
        recommendations = []
        recommendations.extend(get_spending_recommendations(inv_ratio))
        recommendations.extend(get_risk_recommendations(data.risk_level))
        recommendations.extend(get_liquidity_recommendations(data.balance, data.monthly_income))

        return select_top_recommendations(recommendations)
    except Exception as e:
        logger.error(f"Error generating recommendations: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal AI error")

def select_top_recommendations(recs: List[Recommendation]) -> List[Recommendation]:
    """Shuffles and truncates the list to ensure variety and brevity."""
    random.shuffle(recs)
    return recs[:3]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8005)
