package com.proxym.recommendation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role; // USER, ADMIN
    private Integer age;
    private Double monthlyIncome;
    private Double balance;
    private String riskProfile; // Low, Medium, High
    private String financialGoals; // Savings, Investment, Loan
}
