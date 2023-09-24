package tw.pago.pagobackend.dao;

import java.math.BigDecimal;
import java.util.List;
import tw.pago.pagobackend.constant.TransactionTypeEnum;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.PendingWithdrawal;
import tw.pago.pagobackend.model.TransactionRecord;

public interface TransactionDao {
    
    void createTransactionRecord(String orderId, TransactionTypeEnum transactionType, BigDecimal transactionAmount, String userId);

    Integer getWalletBalance(String userId);

    Integer getBalanceAtTransaction(String userId, TransactionRecord transactionRecord);

    List<TransactionRecord> getTransactionRecordList(ListQueryParametersDto listQueryParametersDto);

    List<String> getTransactionDistinctYearMonthByUserId(String userId);

    TransactionRecord getTransactionById(String userId, String transactionId);

    void requestWithdraw(String otpId, String userId, Integer withdrawalAmount);

    PendingWithdrawal getPendingWithdrawalByUserId(String userId);

    void withdraw(String userId, Integer withdrawalAmount);

    void deletePendingWithdrawalById(String pendingWithdrawalId);

    void applyTransactionFee(String userId, Integer transactionFee);


}
