package tw.pago.pagobackend.dao;

import java.math.BigDecimal;
import java.util.List;

import tw.pago.pagobackend.constant.TransactionTypeEnum;
import tw.pago.pagobackend.model.TransactionRecord;

public interface TransactionDao {
    
    void createTransactionRecord(String orderId, TransactionTypeEnum transactionType, BigDecimal transactionAmount, String userId);

    Integer getWalletBalance(String userId);

    List<TransactionRecord> getTransactionList(String userId);

    TransactionRecord getTransactionById(String userId, String transactionId);
}
