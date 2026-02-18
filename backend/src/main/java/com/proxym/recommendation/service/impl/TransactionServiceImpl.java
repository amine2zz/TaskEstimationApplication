package com.proxym.recommendation.service.impl;

import com.proxym.recommendation.dto.TransactionDTO;
import com.proxym.recommendation.exception.ResourceNotFoundException;
import com.proxym.recommendation.model.Transaction;
import com.proxym.recommendation.repository.TransactionRepository;
import com.proxym.recommendation.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing financial transactions.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        return mapToDTO(transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id)));
    }

    @Override
    public TransactionDTO createTransaction(Transaction transaction) {
        ensureTimestamp(transaction);
        return mapToDTO(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDTO updateTransaction(Long id, Transaction transactionDetails) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        copyDetails(existing, transactionDetails);
        return mapToDTO(transactionRepository.save(existing));
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }

    private TransactionDTO mapToDTO(Transaction t) {
        return new TransactionDTO(
                t.getId(),
                t.getUser().getId(),
                t.getAmount(),
                t.getCategory(),
                t.getDate(),
                t.getDescription());
    }

    private void ensureTimestamp(Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
    }

    private void copyDetails(Transaction target, Transaction source) {
        target.setAmount(source.getAmount());
        target.setCategory(source.getCategory());
        target.setDescription(source.getDescription());
        target.setDate(source.getDate());
    }
}
