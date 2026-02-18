package com.proxym.recommendation.controller;

import com.proxym.recommendation.dto.FinancialProductDTO;
import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.service.FinancialProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing the financial product catalog.
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class FinancialProductController {

    @Autowired
    private FinancialProductService productService;

    @GetMapping
    public List<FinancialProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<FinancialProductDTO> createProduct(@RequestBody FinancialProduct product) {
        return ResponseEntity.ok(productService.createProduct(product));
            
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialProductDTO> updateProduct(@PathVariable Long id, @RequestBody FinancialProduct productDetails) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
