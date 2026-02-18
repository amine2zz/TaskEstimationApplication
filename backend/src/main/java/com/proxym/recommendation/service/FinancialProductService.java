package com.proxym.recommendation.service;

import com.proxym.recommendation.dto.FinancialProductDTO;
import com.proxym.recommendation.model.FinancialProduct;
import java.util.List;

public interface FinancialProductService {
    List<FinancialProductDTO> getAllProducts();
    FinancialProductDTO getProductById(Long id);
    FinancialProductDTO createProduct(FinancialProduct product);
    FinancialProductDTO updateProduct(Long id, FinancialProduct productDetails);
    void deleteProduct(Long id);
    FinancialProduct getProductEntityById(Long id);
}
