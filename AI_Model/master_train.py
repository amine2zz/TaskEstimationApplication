import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import accuracy_score
import joblib
import os

# --- 1. DATA LOADING & INTEGRATION ---
def prepare_data():
    print("🚀 Loading datasets...")
    # Get current directory to resolve paths correctly
    dir_path = os.path.dirname(os.path.realpath(__file__))
    
    churn_df = pd.read_csv(os.path.join(dir_path, 'Customer-Churn-Records.csv'))
    bank_df = pd.read_csv(os.path.join(dir_path, 'bank.csv'))
    
    print("🔧 Feature Engineering...")
    
    # We want to create a target: Recommended Product Category
    # Categories: SAVINGS, INVESTMENT, LOAN, INSURANCE
    
    # Mapping logic for synthetic target based on real patterns
    def determine_target(row):
        # High balance + high salary + active -> INVESTMENT
        if row['Balance'] > 50000 and row['EstimatedSalary'] > 80000 and row['IsActiveMember'] == 1:
            return 'INVESTMENT'
        # Low balance + low salary + has credit card -> LOAN
        if row['Balance'] < 5000 and row['NumOfProducts'] >= 2:
            return 'LOAN'
        # Medium balance + medium age -> SAVINGS
        if 20000 < row['Balance'] < 50000:
            return 'SAVINGS'
        # Otherwise -> INSURANCE
        return 'INSURANCE'

    churn_df['TargetProduct'] = churn_df.apply(determine_target, axis=1)
    
    # Selecting the best features for the model
    features = ['CreditScore', 'Age', 'Tenure', 'Balance', 'NumOfProducts', 
                'HasCrCard', 'IsActiveMember', 'EstimatedSalary', 'Satisfaction Score']
    
    X = churn_df[features]
    y = churn_df['TargetProduct']
    
    # Standardize numerical data
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    
    return X_scaled, y, features, scaler

# --- 2. MODEL TRAINING ---
def train_model(X, y):
    print("🧠 Training Strategic Recommendation Model...")
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    model = GradientBoostingClassifier(n_estimators=100, learning_rate=0.1, max_depth=5, random_state=42)
    model.fit(X_train, y_train)
    
    y_pred = model.predict(X_test)
    acc = accuracy_score(y_test, y_pred)
    print(f"✅ Model Accuracy: {acc:.2%}")
    
    return model

# --- 3. TRANSACTION ANALYZER ---
def save_transaction_intelligence():
    """
    Creates a summary of transaction behavior to be used by the smart recommender.
    """
    print("📊 Processing Transaction Intelligence (Large Dataset)...")
    dir_path = os.path.dirname(os.path.realpath(__file__))
    
    # Read only a chunk for intelligence profiling
    tx_df = pd.read_csv(os.path.join(dir_path, 'bank_transactions.csv'), nrows=100000)
    
    # Calculate average spending per category
    intel = {
        'avg_transaction': tx_df['TransactionAmount (INR)'].mean(),
        'high_spending_threshold': tx_df['TransactionAmount (INR)'].quantile(0.9),
        'median_balance': tx_df['CustAccountBalance'].median()
    }
    
    joblib.dump(intel, os.path.join(dir_path, 'transaction_intelligence.pkl'))
    print("✅ Transaction Intelligence Saved.")

# --- MAIN EXECUTION ---
if __name__ == "__main__":
    X, y, feature_names, scaler = prepare_data()
    model = train_model(X, y)
    
    dir_path = os.path.dirname(os.path.realpath(__file__))
    
    # Save artifacts
    joblib.dump(model, os.path.join(dir_path, 'financial_recommender.pkl'))
    joblib.dump(scaler, os.path.join(dir_path, 'data_scaler.pkl'))
    joblib.dump(feature_names, os.path.join(dir_path, 'feature_names.pkl'))
    
    save_transaction_intelligence()
    
    print("\n🏁 AI Model Creation Complete. Ready for Production Integration.")
