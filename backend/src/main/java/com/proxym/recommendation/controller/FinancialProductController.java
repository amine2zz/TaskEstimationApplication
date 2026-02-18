package com.proxym.recommendation.controller;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.service.FinancialProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class FinancialProductController {

    @Autowired
    private FinancialProductService productService;

    @GetMapping
    public List<FinancialProduct> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public FinancialProduct getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public FinancialProduct create(@RequestBody FinancialProduct product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialProduct> update(@PathVariable Long id, @RequestBody FinancialProduct productDetails) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
