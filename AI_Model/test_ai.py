import joblib
import numpy as np
import pandas as pd
import os

def test_model():
    dir_path = os.path.dirname(os.path.realpath(__file__))
    
    print("Testing AI Model and artifacts...")
    model = joblib.load(os.path.join(dir_path, 'financial_recommender.pkl'))
    scaler = joblib.load(os.path.join(dir_path, 'data_scaler.pkl'))
    feature_names = joblib.load(os.path.join(dir_path, 'feature_names.pkl'))
    intel = joblib.load(os.path.join(dir_path, 'transaction_intelligence.pkl'))

    print("\nGlobal Intelligence Benchmarks:")
    print(f" - Average Transaction: {intel['avg_transaction']}")
    print(f" - High Spending Threshold: {intel['high_spending_threshold']}")
    print("-" * 50)

    # Sample Test Cases
    test_cases = [
        {
            "id": "Wealthy and Active",
            "data": [750, 45, 10, 85000, 2, 1, 1, 120000, 5]
        },
        {
            "id": "Young and Low Balance",
            "data": [600, 22, 1, 1500, 3, 1, 0, 25000, 3]
        },
        {
            "id": "Stable Middle Class",
            "data": [680, 38, 5, 35000, 1, 1, 1, 55000, 4]
        }
    ]

    for case in test_cases:
        X = np.array([case['data']])
        X_scaled = scaler.transform(X)
        
        prediction = str(model.predict(X_scaled)[0])
        probabilities = model.predict_proba(X_scaled)[0]
        max_prob = float(np.max(probabilities))

        print(f"Profile: {case['id']}")
        print(f"Prediction: {prediction} ({max_prob})")
        print("-" * 50)

if __name__ == "__main__":
    test_model()
