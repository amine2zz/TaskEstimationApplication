import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import classification_report
import joblib
import os

def train_recommendation_model():
    input_path = os.path.join('AI_Model', 'training_data.csv')
    if not os.path.exists(input_path):
        print("❌ Error: training_data.csv not found. Run data_generator.py first.")
        return

    # 1. Load Data
    df = pd.read_csv(input_path)
    
    # 2. Preprocessing
    # Encode Categorical features
    le_risk = LabelEncoder()
    df['risk_profile'] = le_risk.fit_transform(df['risk_profile'])
    
    le_goal = LabelEncoder()
    df['financial_goal'] = le_goal.fit_transform(df['financial_goal'])
    
    # Define features and target
    X = df.drop(['user_id', 'target_product'], axis=1)
    y = df['target_product']
    
    # 3. Split data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # 4. Train Model
    print("🧠 Training Random Forest Classifier...")
    model = RandomForestClassifier(n_estimators=100, random_state=42)
    model.fit(X_train, y_train)
    
    # 5. Evaluate
    y_pred = model.predict(X_test)
    print("\n📊 Model Evaluation:")
    print(classification_report(y_test, y_pred))
    
    # 6. Save Model and Encoders
    joblib.dump(model, os.path.join('AI_Model', 'recommendation_model.pkl'))
    joblib.dump(le_risk, os.path.join('AI_Model', 'risk_encoder.pkl'))
    joblib.dump(le_goal, os.path.join('AI_Model', 'goal_encoder.pkl'))
    
    print("✅ Model saved to AI_Model/recommendation_model.pkl")

if __name__ == "__main__":
    train_recommendation_model()
