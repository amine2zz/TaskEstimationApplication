package com.proxym.recommendation.controller;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.repository.FinancialProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class FinancialProductController {

    @Autowired
    private FinancialProductRepository productRepository;

    @GetMapping
    public List<FinancialProduct> getAll() {
        return productRepository.findAll();
    }

    @PostMapping
    public FinancialProduct create(@RequestBody FinancialProduct product) {
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialProduct> update(@PathVariable Long id, @RequestBody FinancialProduct productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setType(productDetails.getType());
                    product.setDescription(productDetails.getDescription());
                    product.setInterestRate(productDetails.getInterestRate());
                    product.setMinimumEntry(productDetails.getMinimumEntry());
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
