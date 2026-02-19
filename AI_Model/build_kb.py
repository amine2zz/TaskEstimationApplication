import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import joblib
import os
import numpy as np

class ProxymKnowledgeBase:
    def __init__(self):
        self.dir_path = os.path.dirname(os.path.realpath(__file__))
        self.model = SentenceTransformer('all-MiniLM-L6-v2')
        self.index = None
        self.kb_data = []

    def build_kb(self):
        print("🔍 Building Knowledge Base from CSVs...")
        # Use bank.csv and exploration logs for context
        bank_df = pd.read_csv(os.path.join(self.dir_path, 'bank.csv'))
        
        # Turn financial phrases into searchable context
        contexts = []
        for idx, row in bank_df.head(500).iterrows():
            ctx = f"A client with job {row['job']} and {row['education']} education has a balance of {row['balance']}. Their marital status is {row['marital']}."
            contexts.append(ctx)
        
        # Add general product knowledge
        contexts.append("Savings accounts are best for low-risk clients who want liquidity.")
        contexts.append("Investment portfolios like ETFs are recommended for high-growth and medium-to-high risk profiles.")
        contexts.append("Loans are suitable for major purchases or consolidation when income is stable.")
        contexts.append("Insurance is a protective product for long-term security.")

        self.kb_data = contexts
        embeddings = self.model.encode(contexts)
        
        self.index = faiss.IndexFlatL2(embeddings.shape[1])
        self.index.add(np.array(embeddings).astype('float32'))
        
        # Save the vector DB
        joblib.dump(self.kb_data, os.path.join(self.dir_path, 'kb_metadata.pkl'))
        faiss.write_index(self.index, os.path.join(self.dir_path, 'kb_index.faiss'))
        print("✅ Knowledge Base built and indexed.")

    def search(self, query, k=3):
        query_vector = self.model.encode([query])
        distances, indices = self.index.search(np.array(query_vector).astype('float32'), k)
        return [self.kb_data[i] for i in indices[0]]

if __name__ == "__main__":
    kb = ProxymKnowledgeBase()
    kb.build_kb()
