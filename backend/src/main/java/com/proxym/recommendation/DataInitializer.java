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
        if (userRepository.count() == 0) {
            System.out.println("🌱 Seeding Database with Mock Data...");

            // 1. Create Users
            User john = new User(null, "John Doe", "john@email.com", 32, 5500.0, "Low", "Savings");
            User sarah = new User(null, "Sarah Connor", "sarah@email.com", 28, 7200.0, "High", "Investment");
            userRepository.saveAll(Arrays.asList(john, sarah));

            // 2. Create Products
            FinancialProduct p1 = new FinancialProduct(null, "Secure Yield Savings", "SAVINGS", "High-interest savings account with zero risk.", 4.5, 100.0);
            FinancialProduct p2 = new FinancialProduct(null, "Luxury Growth Portfolio", "INVESTMENT", "Aggressive stock portfolio for high returns.", 12.0, 5000.0);
            FinancialProduct p3 = new FinancialProduct(null, "Flexi-Loan Silver", "LOAN", "Low-interest personal loan for major purchases.", 6.5, 0.0);
            FinancialProduct p4 = new FinancialProduct(null, "Global Tech ETF", "INVESTMENT", "Investment in top tech companies worldwide.", 15.2, 1000.0);
            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4));

            // 3. Create Transactions for John (Low Risk / Savings focus)
            transactionRepository.save(new Transaction(null, john, 1500.0, "Rent", LocalDateTime.now().minusDays(30), "Monthly Rent"));
            transactionRepository.save(new Transaction(null, john, 200.0, "Food", LocalDateTime.now().minusDays(25), "Grocery Store"));
            transactionRepository.save(new Transaction(null, john, 50.0, "Investment", LocalDateTime.now().minusDays(20), "Small bond buy"));

            // 4. Create Transactions for Sarah (High Risk / Investment focus)
            transactionRepository.save(new Transaction(null, sarah, 2000.0, "Investment", LocalDateTime.now().minusDays(15), "Stock purchase"));
            transactionRepository.save(new Transaction(null, sarah, 1500.0, "Investment", LocalDateTime.now().minusDays(10), "Crypto buy"));
            transactionRepository.save(new Transaction(null, sarah, 300.0, "Entertainment", LocalDateTime.now().minusDays(5), "Concert tickets"));

            System.out.println("✅ Database Seeding Complete!");
        } else {
            System.out.println("ℹ️ Database already has data. Skipping seeding.");
        }
    }
}
