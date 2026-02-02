package com.proxym.recommendation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "financial_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // SAVINGS, INVESTMENT, LOAN, INSURANCE
    private String description;
    private Double interestRate;
    private Double minimumEntry;
}
