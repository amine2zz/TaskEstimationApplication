package com.proxym.recommendation.controller;

import com.proxym.recommendation.dto.FinancialProductDTO;
import com.proxym.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for fetching personalized financial product recommendations.
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * Generates and returns a list of recommended products for a user.
     * 
     * @param userId The ID of the user.
     * @return List of DTOs representing the recommended products.
     */
    @GetMapping("/{userId}")
    public List<FinancialProductDTO> getRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendations(userId);
    }
}
