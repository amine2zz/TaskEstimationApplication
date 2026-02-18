package com.proxym.recommendation.service;

import com.proxym.recommendation.exception.ResourceNotFoundException;
import com.proxym.recommendation.model.Transaction;
import com.proxym.recommendation.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing financial transactions, ensuring historical record integrity.
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Retrieves all transactions in the system.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Retrieves transactions belonging to a specific user.
     */
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Fetches a specific transaction by ID.
     */
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    /**
     * Persists a new transaction, applying current timestamp if missing.
     */
    public Transaction createTransaction(Transaction transaction) {
        ensureTimestamp(transaction);
        return transactionRepository.save(transaction);
    }

    /**
     * Updates an existing transaction's details.
     */
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction existing = getTransactionById(id);
        copyTransactionDetails(existing, transactionDetails);
        return transactionRepository.save(existing);
    }

    /**
     * Removes a transaction record.
     */
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }

    private void ensureTimestamp(Transaction transaction) {
        if (isTimestampMissing(transaction)) {
            transaction.setDate(LocalDateTime.now());
        }
    }

    private boolean isTimestampMissing(Transaction t) {
        return t.getDate() == null;
    }

    private void copyTransactionDetails(Transaction target, Transaction source) {
        target.setAmount(source.getAmount());
        target.setCategory(source.getCategory());
        target.setDescription(source.getDescription());
        target.setDate(source.getDate());
    }
}
