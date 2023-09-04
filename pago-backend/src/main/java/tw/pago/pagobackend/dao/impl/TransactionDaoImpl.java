package tw.pago.pagobackend.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.constant.TransactionTypeEnum;
import tw.pago.pagobackend.dao.TransactionDao;
import tw.pago.pagobackend.model.TransactionRecord;
import tw.pago.pagobackend.rowmapper.TransactionRecordRowMapper;

@Component
@AllArgsConstructor
public class TransactionDaoImpl implements TransactionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createTransactionRecord(String orderId, TransactionTypeEnum transactionType, BigDecimal transactionAmount, String userId) {
        String sql = "INSERT INTO transaction_record "
            + "(transaction_id, user_id, transaction_amount, transaction_type, transaction_date, order_id) "
            + "VALUES "
            + "(:transactionId, :userId, :transactionAmount, :transactionType, NOW(), :orderId) ";

        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", "TR" + System.currentTimeMillis());
        map.put("userId", userId);
        map.put("transactionAmount", transactionAmount);
        map.put("transactionType", transactionType.toString());
        map.put("orderId", orderId);

        namedParameterJdbcTemplate.update(sql, map);
    }
    
    @Override
    public Integer getWalletBalance(String userId) {
        String sql = "SELECT SUM(transaction_amount) "
            + "FROM transaction_record "
            + "WHERE user_id = :userId ";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        Integer balance = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        if (balance == null) {
            return 0;
        } else {
            return balance;
        }
    }

    @Override
    public List<TransactionRecord> getTransactionList(String userId) {
        String sql = "SELECT "
            + "tr.transaction_id, tr.user_id, tr.transaction_amount, tr.transaction_type, tr.transaction_date, "
            + "tr.bank_account_id, tr.order_id, "
            + "ba.bank_code, ba.account_number, "
            + "bk.name, "
            + "om.serial_number AS order_serial_number, om.order_item_id, "
            + "oi.name AS order_item_name, "
            + "cr.cancel_reason "
            + "FROM transaction_record tr "
            + "LEFT JOIN bank_account ba ON tr.bank_account_id = ba.bank_account_id "
            + "LEFT JOIN bank bk ON ba.bank_code = bk.bank_code "
            + "LEFT JOIN order_main om ON tr.order_id = om.order_id "
            + "LEFT JOIN order_item oi ON om.order_item_id = oi.order_item_id "
            + "LEFT JOIN cancellation_record cr ON om.order_id = cr.order_id "
            + "WHERE tr.user_id = :userId ";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<TransactionRecord> transactionRecordList = namedParameterJdbcTemplate.query(sql, map, new TransactionRecordRowMapper());

        return transactionRecordList;
    }

    @Override
    public TransactionRecord getTransactionById(String userId, String transactionId) {
        String sql = "SELECT "
            + "tr.transaction_id, tr.user_id, tr.transaction_amount, tr.transaction_type, tr.transaction_date, "
            + "tr.bank_account_id, tr.order_id, "
            + "ba.bank_code, ba.account_number, "
            + "bk.name, "
            + "om.serial_number AS order_serial_number, om.order_item_id, "
            + "oi.name AS order_item_name, "
            + "cr.cancel_reason "
            + "FROM transaction_record tr "
            + "LEFT JOIN bank_account ba ON tr.bank_account_id = ba.bank_account_id "
            + "LEFT JOIN bank bk ON ba.bank_code = bk.bank_code "
            + "LEFT JOIN order_main om ON tr.order_id = om.order_id "
            + "LEFT JOIN order_item oi ON om.order_item_id = oi.order_item_id "
            + "LEFT JOIN cancellation_record cr ON om.order_id = cr.order_id "
            + "WHERE tr.user_id = :userId "
            + "AND tr.transaction_id = :transactionId ";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("transactionId", transactionId);

        List<TransactionRecord> transactionRecordList = namedParameterJdbcTemplate.query(sql, map, new TransactionRecordRowMapper());

        if (transactionRecordList.size() > 0) {
            return transactionRecordList.get(0);
        } else {
            return null;
        }
    }
    
}
