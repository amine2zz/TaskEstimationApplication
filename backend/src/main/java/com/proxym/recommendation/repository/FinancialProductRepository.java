package com.proxym.recommendation.repository;

import com.proxym.recommendation.model.FinancialProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialProductRepository extends JpaRepository<FinancialProduct, Long> {
}
