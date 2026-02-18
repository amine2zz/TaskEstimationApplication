package com.proxym.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialProductDTO {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Double interestRate;
    private Double minimumEntry;
}
