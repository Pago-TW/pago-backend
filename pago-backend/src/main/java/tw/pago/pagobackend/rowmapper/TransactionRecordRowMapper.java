package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;

import tw.pago.pagobackend.constant.TransactionTypeEnum;
import tw.pago.pagobackend.model.TransactionRecord;

public class TransactionRecordRowMapper implements RowMapper<TransactionRecord> {
    
    @Override
    public TransactionRecord mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return TransactionRecord.builder()
            .transactionId(resultSet.getString("transaction_id"))
            .userId(resultSet.getString("user_id"))
            .transactionAmount(resultSet.getInt("transaction_amount"))
            .transactionType(TransactionTypeEnum.valueOf(resultSet.getString("transaction_type")))
            .transactionDate(resultSet.getTimestamp("transaction_date").toInstant().atZone(ZoneId.of("UTC")))
            .transactionStatus(resultSet.getString("transaction_status"))
            .bankAccountId(resultSet.getString("bank_account_id"))
            .accountNumber(resultSet.getString("account_number"))
            .bankName(resultSet.getString("name"))
            .orderId(resultSet.getString("order_id"))
            .orderSerialNumber(resultSet.getString("order_serial_number"))
            .orderName(resultSet.getString("order_item_name"))
            .cancelReason(resultSet.getString("cancel_reason"))
            .build();
    }
}
