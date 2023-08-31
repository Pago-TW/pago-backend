package tw.pago.pagobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.TransactionDao;
import tw.pago.pagobackend.model.TransactionRecord;
import tw.pago.pagobackend.service.TransactionService;

@Component
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionDao transactionDao;

    @Override
    public Integer getWalletBalance(String userId) {
        return transactionDao.getWalletBalance(userId);
    }

    @Override
    public List<TransactionRecord> getTransactionList(String userId) {
        return transactionDao.getTransactionList(userId);
    }

    @Override
    public TransactionRecord getTransactionById(String userId, String transactionId) {
        return transactionDao.getTransactionById(userId, transactionId);
    }
}
