package com.proxym.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Integer age;
    private Double monthlyIncome;
    private Double balance;
    private String riskProfile;
    private String financialGoals;
}
