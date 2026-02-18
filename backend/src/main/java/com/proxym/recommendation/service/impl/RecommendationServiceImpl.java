package com.proxym.recommendation.service.impl;

import com.proxym.recommendation.dto.FinancialProductDTO;
import com.proxym.recommendation.dto.TransactionDTO;
import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.FinancialProductRepository;
import com.proxym.recommendation.service.RecommendationService;
import com.proxym.recommendation.service.TransactionService;
import com.proxym.recommendation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enterprise recommendation engine with pluggable AI module support.
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FinancialProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.module.url:http://localhost:8005/recommend}")
    private String aiModuleUrl;

    @Override
    public List<FinancialProductDTO> getRecommendations(Long userId) {
        User user = userService.getUserEntityById(userId);
        List<TransactionDTO> transactionDTOs = transactionService.getTransactionsByUserId(userId);

        double investmentRatio = calculateInvestmentRatio(transactionDTOs);
        return attemptAiRecommendations(userId, user, investmentRatio);
    }

    private List<FinancialProductDTO> attemptAiRecommendations(Long userId, User user, double ratio) {
        try {
            Map<String, Object> aiRequest = prepareAiRequest(userId, user, ratio);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> aiResult = restTemplate.postForObject(aiModuleUrl, aiRequest, List.class);

            if (aiResult != null && !aiResult.isEmpty()) {
                List<String> suggestedNames = aiResult.stream()
                        .map(r -> (String) r.get("product_name"))
                        .collect(Collectors.toList());

                return productRepository.findAll().stream()
                        .filter(p -> suggestedNames.contains(p.getName()))
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("AI Error: " + e.getMessage());
        }
        return getFallbackRecommendations(ratio);
    }

    private double calculateInvestmentRatio(List<TransactionDTO> transactions) {
        if (transactions == null || transactions.isEmpty())
            return 0.0;
        double total = transactions.stream().mapToDouble(TransactionDTO::getAmount).sum();
        if (total <= 0)
            return 0.0;

        double inv = transactions.stream()
                .filter(t -> "Investment".equalsIgnoreCase(t.getCategory()))
                .mapToDouble(TransactionDTO::getAmount)
                .sum();
        return inv / total;
    }

    private List<FinancialProductDTO> getFallbackRecommendations(double ratio) {
        String type = ratio > 0.2 ? "INVESTMENT" : "SAVINGS";
        return productRepository.findAll().stream()
                .filter(p -> type.equals(p.getType()))
                .limit(3)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Map<String, Object> prepareAiRequest(Long userId, User user, double ratio) {
        Map<String, Object> req = new HashMap<>();
        req.put("user_id", userId);
        req.put("spending_habits", Map.of("investment_ratio", ratio));
        req.put("risk_level", user.getRiskProfile());
        req.put("balance", user.getBalance());
        req.put("monthly_income", user.getMonthlyIncome());
        return req;
    }

    private FinancialProductDTO mapToDTO(FinancialProduct p) {
        return new FinancialProductDTO(p.getId(), p.getName(), p.getType(), p.getDescription(), p.getInterestRate(),
                p.getMinimumEntry());
    }
}
