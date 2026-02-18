package com.proxym.recommendation;

import com.proxym.recommendation.model.FinancialProduct;
import com.proxym.recommendation.model.Transaction;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.service.FinancialProductService;
import com.proxym.recommendation.service.TransactionService;
import com.proxym.recommendation.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Handles initial database population with realistic mock data for 
 * users, products, and transaction history.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final TransactionService transactionService;
    private final FinancialProductService productService;

    public DataInitializer(UserService userService, 
                           TransactionService transactionService, 
                           FinancialProductService productService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseAlreadySeeded()) {
            System.out.println("✅ Database already has data. Skipping re-seeding.");
            return;
        }

        performInitialSeeding();
    }

    private boolean isDatabaseAlreadySeeded() {
        return userService.getAllUsers().size() > 0;
    }

    private void performInitialSeeding() {
        System.out.println("🌱 Database is empty. Seeding with Extensive Mock Data...");

        List<User> users = seedUsers();
        seedProducts();
        seedTransactions(users);

        System.out.println("✅ Database Seeding Complete with 6 Months of Data!");
    }

    private List<User> seedUsers() {
        return Arrays.asList(
            userService.createUser(new User(null, "Admin User", "admin@proxym.com", "admin", "ADMIN", 40, 0.0, 100000.0, "Low", "Management")),
            userService.createUser(new User(null, "John Doe", "john@email.com", "password", "USER", 32, 5500.0, 45280.0, "Low", "Savings")),
            userService.createUser(new User(null, "Sarah Connor", "sarah@email.com", "password", "USER", 28, 8200.0, 12400.0, "High", "Investment")),
            userService.createUser(new User(null, "Mike Ross", "mike@email.com", "password", "USER", 35, 6000.0, 8500.0, "Medium", "Loan Management")),
            userService.createUser(new User(null, "Amine", "amine@gmail.com", "password", "USER", 30, 7500.0, 15000.0, "Medium", "Growth"))
        );
    }

    private void seedProducts() {
        productService.createProduct(new FinancialProduct(null, "Secure Yield Savings", "SAVINGS", "High-interest savings account with zero risk.", 4.5, 100.0));
        productService.createProduct(new FinancialProduct(null, "Luxury Growth Portfolio", "INVESTMENT", "Aggressive stock portfolio for high returns.", 12.0, 5000.0));
        productService.createProduct(new FinancialProduct(null, "Flexi-Loan Silver", "LOAN", "Low-interest personal loan for major purchases.", 6.5, 0.0));
        productService.createProduct(new FinancialProduct(null, "Global Tech ETF", "INVESTMENT", "Investment in top tech companies worldwide.", 15.2, 1000.0));
        productService.createProduct(new FinancialProduct(null, "Premium Plus Savings", "SAVINGS", "Exclusive savings with higher yield for large balances.", 5.2, 10000.0));
        productService.createProduct(new FinancialProduct(null, "Real Estate REIT", "INVESTMENT", "Diversified real estate investment trust.", 8.5, 500.0));
    }

    private void seedTransactions(List<User> users) {
        users.stream()
            .filter(this::isNotAdmin)
            .forEach(this::seedUserData);
    }

    private boolean isNotAdmin(User user) {
        return !"ADMIN".equals(user.getRole());
    }

    private void seedUserData(User user) {
        for (int month = 0; month < 6; month++) {
            LocalDateTime baseDate = LocalDateTime.now().minusMonths(month);
            seedMonthlyCycle(user, baseDate);
        }
    }

    private void seedMonthlyCycle(User user, LocalDateTime monthDate) {
        saveMonthlyRent(user, monthDate);
        seedRandomDailyActivity(user, monthDate);
    }

    private void saveMonthlyRent(User user, LocalDateTime date) {
        double rentAmount = 1200.0 + (Math.random() * 300);
        transactionService.createTransaction(new Transaction(null, user, rentAmount, "Rent", date.withDayOfMonth(1), "Monthly Rent"));
    }

    private void seedRandomDailyActivity(User user, LocalDateTime date) {
        String[] defaultCategories = {"Food", "Subscription", "Utilities", "Entertainment", "Shopping", "Transport"};
        for (int i = 0; i < 15; i++) {
            processRandomTransaction(user, date, i, defaultCategories);
        }
    }

    private void processRandomTransaction(User user, LocalDateTime monthDate, int index, String[] categories) {
        String category = pickCategory(user, categories);
        double amount = calculateRandomAmount(user);
        LocalDateTime txDate = monthDate.withDayOfMonth(generateRandomDay());
        
        transactionService.createTransaction(new Transaction(null, user, amount, category, txDate, "Transaction " + index));
    }

    private String pickCategory(User user, String[] defaults) {
        if (isAggressiveInvestor(user)) return "Investment";
        if (isConservativeSaver(user)) return "Savings";
        return defaults[(int)(Math.random() * defaults.length)];
    }

    private double calculateRandomAmount(User user) {
        if (isAggressiveInvestor(user)) return 1000.0 + (Math.random() * 2000);
        if (isConservativeSaver(user)) return 500.0;
        return Math.round((10 + (Math.random() * 200)) * 100.0) / 100.0;
    }

    private boolean isAggressiveInvestor(User user) {
        return user.getName().contains("Sarah") && Math.random() > 0.6;
    }

    private boolean isConservativeSaver(User user) {
        return user.getName().contains("John") && Math.random() > 0.8;
    }

    private int generateRandomDay() {
        return 1 + (int)(Math.random() * 27);
    }
}
