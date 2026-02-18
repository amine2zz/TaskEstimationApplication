package com.proxym.recommendation.service.impl;

import com.proxym.recommendation.dto.FinancialProductDTO;
import com.proxym.recommendation.exception.ResourceNotFoundException;
import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.repository.FinancialProductRepository;
import com.proxym.recommendation.service.FinancialProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing the financial product catalog.
 */
@Service
public class FinancialProductServiceImpl implements FinancialProductService {

    @Autowired
    private FinancialProductRepository productRepository;

    @Override
    public List<FinancialProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FinancialProductDTO getProductById(Long id) {
        return mapToDTO(getProductEntityById(id));
    }

    @Override
    public FinancialProduct getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial Product not found with id: " + id));
    }

    @Override
    public FinancialProductDTO createProduct(FinancialProduct product) {
        return mapToDTO(productRepository.save(product));
    }

    @Override
    public FinancialProductDTO updateProduct(Long id, FinancialProduct productDetails) {
        FinancialProduct product = getProductEntityById(id);
        copyProductDetails(product, productDetails);
        return mapToDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        FinancialProduct product = getProductEntityById(id);
        productRepository.delete(product);
    }

    private FinancialProductDTO mapToDTO(FinancialProduct product) {
        return new FinancialProductDTO(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getDescription(),
                product.getInterestRate(),
                product.getMinimumEntry());
    }

    private void copyProductDetails(FinancialProduct target, FinancialProduct source) {
        target.setName(source.getName());
        target.setType(source.getType());
        target.setDescription(source.getDescription());
        target.setInterestRate(source.getInterestRate());
        target.setMinimumEntry(source.getMinimumEntry());
    }
}
