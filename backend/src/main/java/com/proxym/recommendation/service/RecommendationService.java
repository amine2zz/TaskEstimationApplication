package com.proxym.recommendation.service;

import com.proxym.recommendation.dto.FinancialProductDTO;
import java.util.List;

public interface RecommendationService {
    List<FinancialProductDTO> getRecommendations(Long userId);
}
