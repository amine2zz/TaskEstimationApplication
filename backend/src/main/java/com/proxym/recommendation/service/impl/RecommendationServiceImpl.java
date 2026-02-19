package com.proxym.recommendation.service.impl;

import com.proxym.recommendation.dto.FinancialProductDTO;
import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.FinancialProductRepository;
import com.proxym.recommendation.service.RecommendationService;
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

        // 1. Get raw strategic advice from AI Module
        String suggestedType = callAiForStrategy(user);
        System.out.println("🤖 AI Suggested Strategy for " + user.getName() + ": " + suggestedType);

        // 2. Map AI category to real database products
        return productRepository.findByType(suggestedType).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private String callAiForStrategy(User user) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("credit_score", 700); // Default if not in user model
            req.put("age", user.getAge());
            req.put("tenure", 5);
            req.put("balance", user.getBalance());
            req.put("num_products", 2);
            req.put("has_crcard", 1);
            req.put("is_active", 1);
            req.put("salary", user.getMonthlyIncome());
            req.put("satisfaction", 5);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(aiModuleUrl, req, Map.class);
            if (response != null && response.containsKey("prediction")) {
                return (String) response.get("prediction");
            }
        } catch (Exception e) {
            System.err.println("⚠️ AI Strategic Module unreachable, using fallback. Error: " + e.getMessage());
        }
        return user.getBalance() > 5000 ? "INVESTMENT" : "SAVINGS"; // Hard fallback
    }

    private FinancialProductDTO mapToDTO(FinancialProduct p) {
        return new FinancialProductDTO(p.getId(), p.getName(), p.getType(), p.getDescription(), p.getInterestRate(),
                p.getMinimumEntry());
    }
}
