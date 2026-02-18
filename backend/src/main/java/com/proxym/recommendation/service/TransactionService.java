package com.proxym.recommendation.service;

import com.proxym.recommendation.dto.TransactionDTO;
import com.proxym.recommendation.model.Transaction;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();

    List<TransactionDTO> getTransactionsByUserId(Long userId);

    TransactionDTO getTransactionById(Long id);

    TransactionDTO createTransaction(Transaction transaction);

    TransactionDTO updateTransaction(Long id, Transaction transactionDetails);

    void deleteTransaction(Long id);
}
