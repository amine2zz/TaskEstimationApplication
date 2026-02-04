package com.proxym.recommendation;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.model.Transaction;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.FinancialProductRepository;
import com.proxym.recommendation.repository.TransactionRepository;
import com.proxym.recommendation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final FinancialProductRepository productRepository;

    public DataInitializer(UserRepository userRepository, 
                           TransactionRepository transactionRepository, 
                           FinancialProductRepository productRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println("✅ Database already has data. Skipping re-seeding.");
            return;
        }

        System.out.println("🌱 Database is empty. Seeding with Extensive Mock Data...");

            // 1. Create Users
            User admin = new User(null, "Admin User", "admin@proxym.com", "admin", "ADMIN", 40, 0.0, 100000.0, "Low", "Management");
            User john = new User(null, "John Doe", "john@email.com", "password", "USER", 32, 5500.0, 45280.0, "Low", "Savings");
            User sarah = new User(null, "Sarah Connor", "sarah@email.com", "password", "USER", 28, 8200.0, 12400.0, "High", "Investment");
            User mike = new User(null, "Mike Ross", "mike@email.com", "password", "USER", 35, 6000.0, 8500.0, "Medium", "Loan Management");
            User amine = new User(null, "Amine", "amine@gmail.com", "password", "USER", 30, 7500.0, 15000.0, "Medium", "Growth");
            userRepository.saveAll(Arrays.asList(admin, john, sarah, mike, amine));

            // 2. Create Products
            FinancialProduct p1 = new FinancialProduct(null, "Secure Yield Savings", "SAVINGS", "High-interest savings account with zero risk.", 4.5, 100.0);
            FinancialProduct p2 = new FinancialProduct(null, "Luxury Growth Portfolio", "INVESTMENT", "Aggressive stock portfolio for high returns.", 12.0, 5000.0);
            FinancialProduct p3 = new FinancialProduct(null, "Flexi-Loan Silver", "LOAN", "Low-interest personal loan for major purchases.", 6.5, 0.0);
            FinancialProduct p4 = new FinancialProduct(null, "Global Tech ETF", "INVESTMENT", "Investment in top tech companies worldwide.", 15.2, 1000.0);
            FinancialProduct p5 = new FinancialProduct(null, "Premium Plus Savings", "SAVINGS", "Exclusive savings with higher yield for large balances.", 5.2, 10000.0);
            FinancialProduct p6 = new FinancialProduct(null, "Real Estate REIT", "INVESTMENT", "Diversified real estate investment trust.", 8.5, 500.0);
            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6));

            // 3. Generate 6 Months of transaction history
            String[] categories = {"Food", "Rent", "Subscription", "Utilities", "Investment", "Entertainment", "Shopping", "Transport"};
            
            for (User user : Arrays.asList(john, sarah, mike, amine)) {
                for (int month = 0; month < 6; month++) {
                    LocalDateTime baseDate = LocalDateTime.now().minusMonths(month);
                    
                    // Fixed Monthly Rent
                    transactionRepository.save(new Transaction(null, user, 1200.0 + (Math.random() * 300), "Rent", baseDate.withDayOfMonth(1), "Monthly Rent"));
                    
                    // Random daily/weekly transactions
                    for (int i = 0; i < 15; i++) {
                        String cat = categories[(int)(Math.random() * categories.length)];
                        double amount = 10 + (Math.random() * 200);
                        
                        // Override for specific user profiles
                        if (user.getName().contains("John") && Math.random() > 0.8) {
                            cat = "Savings"; // John likes to save
                            amount = 500;
                        } else if (user.getName().contains("Sarah") && Math.random() > 0.6) {
                            cat = "Investment"; // Sarah is aggressive
                            amount = 1000 + (Math.random() * 2000);
                        }
                        
                        transactionRepository.save(new Transaction(null, user, Math.round(amount * 100.0) / 100.0, cat, baseDate.withDayOfMonth(1 + (int)(Math.random() * 27)), "Transaction " + i));
                    }
                }
            }

            System.out.println("✅ Database Seeding Complete with 6 Months of Data!");
    }
}
