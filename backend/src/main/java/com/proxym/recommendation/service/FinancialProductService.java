package com.proxym.recommendation.service;

import com.proxym.recommendation.exception.ResourceNotFoundException;
import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.repository.FinancialProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service managing the financial product catalog.
 */
@Service
public class FinancialProductService {

    @Autowired
    private FinancialProductRepository productRepository;

    /**
     * Retrieves all available financial products.
     */
    public List<FinancialProduct> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a specific product by its ID.
     */
    public FinancialProduct getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial Product not found with id: " + id));
    }

    /**
     * Registers a new financial product in the catalog.
     */
    public FinancialProduct createProduct(FinancialProduct product) {
        return productRepository.save(product);
    }

    /**
     * Updates product details.
     */
    public FinancialProduct updateProduct(Long id, FinancialProduct productDetails) {
        FinancialProduct product = getProductById(id);
        copyProductDetails(product, productDetails);
        return productRepository.save(product);
    }

    /**
     * Deletes a product from the catalog.
     */
    public void deleteProduct(Long id) {
        FinancialProduct product = getProductById(id);
        productRepository.delete(product);
    }

    private void copyProductDetails(FinancialProduct target, FinancialProduct source) {
        target.setName(source.getName());
        target.setType(source.getType());
        target.setDescription(source.getDescription());
        target.setInterestRate(source.getInterestRate());
        target.setMinimumEntry(source.getMinimumEntry());
    }
}
