package tw.pago.pagobackend.dao.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.constant.TransactionStatusEnum;
import tw.pago.pagobackend.constant.TransactionTypeEnum;
import tw.pago.pagobackend.dao.TransactionDao;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.PendingWithdrawal;
import tw.pago.pagobackend.model.TransactionRecord;
import tw.pago.pagobackend.rowmapper.PendingWithdrawalRowMapper;
import tw.pago.pagobackend.rowmapper.TransactionRecordRowMapper;
import tw.pago.pagobackend.rowmapper.TransactionYearMonthRowMapper;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
@AllArgsConstructor
public class TransactionDaoImpl implements TransactionDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final UuidGenerator uuidGenerator;

  @Override
  public void createTransactionRecord(String orderId, TransactionTypeEnum transactionType,
      BigDecimal transactionAmount, String userId) {
    String sql = "INSERT INTO transaction_record "
        + "(transaction_id, user_id, transaction_amount, transaction_type, transaction_date, order_id) "
        + "VALUES "
        + "(:transactionId, :userId, :transactionAmount, :transactionType, NOW(), :orderId) ";

    Map<String, Object> map = new HashMap<>();
    map.put("transactionId", uuidGenerator.getUuid());
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
  public Integer getBalanceAtTransaction(String userId, TransactionRecord transactionRecord) {

    ZonedDateTime transactionDate = transactionRecord.getTransactionDate();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDate = transactionDate.format(formatter);


    // Then, sum up all transactions that happened up to and including that time
    String sql = "SELECT SUM(transaction_amount) "
        + "FROM transaction_record "
        + "WHERE user_id = :userId "
        + "AND transaction_date <= :transactionDate ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);
    map.put("transactionDate", formattedDate);

    Integer balance = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    if (balance == null) {
      return 0;
    } else {
      return balance;
    }
  }




  @Override
  public List<TransactionRecord> getTransactionRecordList(
      ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT "
        + "tr.transaction_id, tr.user_id, tr.transaction_amount, tr.transaction_type, tr.transaction_date, tr.transaction_status, "
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
    map.put("userId", listQueryParametersDto.getUserId());

    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + " transaction_date " + " "
        + " DESC ";

    return namedParameterJdbcTemplate.query(sql, map, new TransactionRecordRowMapper());
  }

  @Override
  public List<String> getTransactionDistinctYearMonthByUserId(String userId) {
    String sql = "SELECT DISTINCT DATE_FORMAT(transaction_date, '%Y-%m') as transaction_ym "
        + "FROM transaction_record "
        + "WHERE user_id = :userId "
        + "ORDER BY transaction_ym DESC";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);

    return namedParameterJdbcTemplate.query(sql, map, new TransactionYearMonthRowMapper());
  }


  @Override
  public TransactionRecord getTransactionById(String userId, String transactionId) {
    String sql = "SELECT "
        + "tr.transaction_id, tr.user_id, tr.transaction_amount, tr.transaction_type, tr.transaction_date, tr.transaction_status, "
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

    List<TransactionRecord> transactionRecordList = namedParameterJdbcTemplate.query(sql, map,
        new TransactionRecordRowMapper());

    if (!transactionRecordList.isEmpty()) {
      return transactionRecordList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void requestWithdraw(String otpId, String userId, Integer withdrawalAmount) {
    String sql = "INSERT INTO pending_withdrawal "
        + "(pending_withdrawal_id, user_id, withdrawal_amount) "
        + "VALUES "
        + "(:pendingWithdrawalId, :userId, :withdrawalAmount) ";

    Map<String, Object> map = new HashMap<>();
    map.put("pendingWithdrawalId", uuidGenerator.getUuid());
    map.put("userId", userId);
    map.put("transactionAmount", withdrawalAmount);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public PendingWithdrawal getPendingWithdrawalByUserId(String userId) {
    String sql = "SELECT "
        + "pending_withdrawal_id, user_id, withdrawal_amount "
        + "FROM pending_withdrawal "
        + "WHERE user_id = :userId ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);

    List<PendingWithdrawal> pendingWithdrawalList = namedParameterJdbcTemplate.query(sql, map,
        new PendingWithdrawalRowMapper());

    if (!pendingWithdrawalList.isEmpty()) {
      return pendingWithdrawalList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void withdraw(String userId, Integer withdrawalAmount, String bankAccountId) {
    String sql = "INSERT INTO transaction_record "
        + "(transaction_id, user_id, transaction_amount, transaction_type, bank_account_id, transaction_date, transaction_status) "
        + "VALUES "
        + "(:transactionId, :userId, :transactionAmount, :transactionType, :bank_account_id, NOW(), :transactionStatus) ";

    Map<String, Object> map = new HashMap<>();
    map.put("transactionId", uuidGenerator.getUuid());
    map.put("userId", userId);
    map.put("transactionAmount", withdrawalAmount);
    map.put("transactionType", TransactionTypeEnum.WITHDRAW.toString());
    map.put("transactionStatus", TransactionStatusEnum.WITHDRAWAL_IN_PROGRESS.toString());
    map.put("bank_account_id", bankAccountId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void deletePendingWithdrawalById(String pendingWithdrawalId) {
    String sql = "DELETE FROM pending_withdrawal "
        + "WHERE pending_withdrawal_id = :pendingWithdrawalId ";

    Map<String, Object> map = new HashMap<>();
    map.put("pendingWithdrawalId", pendingWithdrawalId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void applyTransactionFee(String userId, Integer transactionFee) {
    String sql = "INSERT INTO transaction_record "
        + "(transaction_id, user_id, transaction_amount, transaction_type, transaction_date) "
        + "VALUES "
        + "(:transactionId, :userId, :transactionAmount, :transactionType, NOW()) ";

    Map<String, Object> map = new HashMap<>();
    map.put("transactionId", uuidGenerator.getUuid());
    map.put("userId", userId);
    map.put("transactionAmount", transactionFee);
    map.put("transactionType", TransactionTypeEnum.FEE.toString());

    namedParameterJdbcTemplate.update(sql, map);
  }

  private String addFilteringSql(String sql, Map<String, Object> map,
      ListQueryParametersDto listQueryParametersDto) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    ZonedDateTime startDate = listQueryParametersDto.getStartDate();
    ZonedDateTime endDate = listQueryParametersDto.getEndDate();

    if (startDate != null) {
      String startDateStr = startDate.format(formatter);

      sql = sql + " AND transaction_date >= :startDate ";
      map.put("startDate", startDateStr);
    }

    if (endDate != null) {
      String endDateStr = endDate.format(formatter);

      sql = sql + " AND transaction_date <= :endDate ";
      map.put("endDate", endDateStr);
    }

    return sql;
  }


}
