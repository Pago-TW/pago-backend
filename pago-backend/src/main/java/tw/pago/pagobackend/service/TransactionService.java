package tw.pago.pagobackend.service;

import java.util.List;

import tw.pago.pagobackend.model.TransactionRecord;

public interface TransactionService {
    Integer getWalletBalance(String userId);

    List<TransactionRecord> getTransactionList(String userId);

    TransactionRecord getTransactionById(String userId, String transactionId);
}