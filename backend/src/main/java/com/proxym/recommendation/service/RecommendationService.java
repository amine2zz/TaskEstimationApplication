package com.proxym.recommendation.service;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.model.Transaction;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.FinancialProductRepository;
import com.proxym.recommendation.repository.TransactionRepository;
import com.proxym.recommendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FinancialProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

    private static final String AI_MODULE_URL = "http://localhost:8005/recommend";

    public List<FinancialProduct> getRecommendations(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        
        // Calculate basic metrics for AI
        double totalSpend = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        double investmentSpend = transactions.stream()
                .filter(t -> "Investment".equalsIgnoreCase(t.getCategory()))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        double ratio = totalSpend > 0 ? investmentSpend / totalSpend : 0;

        // Prepare request for AI
        Map<String, Object> aiRequest = new HashMap<>();
        aiRequest.put("user_id", userId);
        aiRequest.put("spending_habits", Map.of("investment_ratio", ratio));
        aiRequest.put("risk_level", "Medium"); // Default for now

        try {
            // Call Python AI
            List<Map<String, Object>> aiResult = restTemplate.postForObject(AI_MODULE_URL, aiRequest, List.class);
            
            if (aiResult != null && !aiResult.isEmpty()) {
                List<String> suggestedNames = aiResult.stream()
                        .map(r -> (String) r.get("product_name"))
                        .collect(Collectors.toList());
                
                // Fetch real products from DB that match AI suggestions
                return productRepository.findAll().stream()
                        .filter(p -> suggestedNames.contains(p.getName()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("AI Module error: " + e.getMessage());
        }

        // Fallback to simple logic if AI fails
        return productRepository.findAll().stream()
                .filter(p -> ratio > 0.2 ? "INVESTMENT".equals(p.getType()) : "SAVINGS".equals(p.getType()))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<FinancialProduct> getAllProducts() {
        return productRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
