package com.proxym.recommendation.service;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.model.Transaction;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.FinancialProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Orchestrates the recommendation engine by integrating user data, 
 * transaction history, and external AI analysis.
 */
@Service
public class RecommendationService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FinancialProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String AI_MODULE_URL = "http://localhost:8005/recommend";

    /**
     * Entry point for generating personalized financial product recommendations.
     * Attempts to use AI analysis and falls back to heuristic-based logic.
     * 
     * @param userId The ID of the user for whom to generate recommendations.
     * @return A list of recommended financial products.
     */
    public List<FinancialProduct> getRecommendations(Long userId) {
        User user = userService.getUserById(userId);
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        
        double investmentRatio = calculateInvestmentRatio(transactions);
        
        return attemptAiRecommendations(userId, user, investmentRatio);
    }

    private List<FinancialProduct> attemptAiRecommendations(Long userId, User user, double ratio) {
        try {
            Map<String, Object> aiRequest = prepareAiRequest(userId, user, ratio);
            List<FinancialProduct> aiRecs = callAiModule(aiRequest);
            
            if (hasResults(aiRecs)) {
                return aiRecs;
            }
        } catch (Exception e) {
            logAiError(e);
        }
        return getFallbackRecommendations(ratio);
    }

    private double calculateInvestmentRatio(List<Transaction> transactions) {
        double totalSpend = calculateTotalSpend(transactions);
        if (isTotalSpendZero(totalSpend)) return 0;

        double investmentSpend = calculateInvestmentSpend(transactions);
        return investmentSpend / totalSpend;
    }

    private double calculateTotalSpend(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    private double calculateInvestmentSpend(List<Transaction> transactions) {
        return transactions.stream()
                .filter(this::isInvestmentCategory)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private boolean isInvestmentCategory(Transaction t) {
        return "Investment".equalsIgnoreCase(t.getCategory());
    }

    private boolean isTotalSpendZero(double total) {
        return total <= 0;
    }

    private Map<String, Object> prepareAiRequest(Long userId, User user, double ratio) {
        Map<String, Object> aiRequest = new HashMap<>();
        aiRequest.put("user_id", userId);
        aiRequest.put("spending_habits", Map.of("investment_ratio", ratio));
        aiRequest.put("risk_level", user.getRiskProfile()); 
        aiRequest.put("balance", user.getBalance());
        aiRequest.put("monthly_income", user.getMonthlyIncome());
        return aiRequest;
    }

    @SuppressWarnings("unchecked")
    private List<FinancialProduct> callAiModule(Map<String, Object> aiRequest) {
        List<Map<String, Object>> aiResult = restTemplate.postForObject(AI_MODULE_URL, aiRequest, List.class);
        return mapAiResultToFinancialProducts(aiResult);
    }

    private List<FinancialProduct> mapAiResultToFinancialProducts(List<Map<String, Object>> aiResult) {
        if (isNullOrEmpty(aiResult)) {
            return List.of();
        }

        List<String> suggestedNames = extractProductNames(aiResult);
        return findProductsByNames(suggestedNames);
    }

    private List<String> extractProductNames(List<Map<String, Object>> results) {
        return results.stream()
                .map(r -> (String) r.get("product_name"))
                .collect(Collectors.toList());
    }

    private List<FinancialProduct> findProductsByNames(List<String> names) {
        return productRepository.findAll().stream()
                .filter(p -> names.contains(p.getName()))
                .collect(Collectors.toList());
    }

    private List<FinancialProduct> getFallbackRecommendations(double ratio) {
        String preferredType = determinePreferredType(ratio);
        return productRepository.findAll().stream()
                .filter(p -> p.getType().equals(preferredType))
                .limit(3)
                .collect(Collectors.toList());
    }

    private String determinePreferredType(double ratio) {
        return ratio > 0.2 ? "INVESTMENT" : "SAVINGS";
    }

    private boolean hasResults(List<?> list) {
        return list != null && !list.isEmpty();
    }

    private boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private void logAiError(Exception e) {
        System.err.println("AI Module error context: " + e.getMessage());
    }
}
