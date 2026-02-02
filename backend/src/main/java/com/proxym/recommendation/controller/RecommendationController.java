package com.proxym.recommendation.controller;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public List<FinancialProduct> getRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendations(userId);
    }

    @GetMapping("/products")
    public List<FinancialProduct> getAllProducts() {
        return recommendationService.getAllProducts();
    }

    @GetMapping("/users")
    public List<com.proxym.recommendation.model.User> getAllUsers() {
        return recommendationService.getAllUsers();
    }

    @GetMapping("/transactions")
    public List<com.proxym.recommendation.model.Transaction> getAllTransactions() {
        return recommendationService.getAllTransactions();
    }
}
