package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.CancellationRecordDao;
import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.rowmapper.CancellationRecordRowMapper;
import tw.pago.pagobackend.rowmapper.ChatroomRowMapper;

@Repository
@AllArgsConstructor
public class CancellationRecordDaoImpl implements CancellationRecordDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public int countCancellationRecord(String userId) {
    String sql = "SELECT COUNT(cancellation_record_id) "
        + "FROM cancellation_record "
        + "WHERE user_id = :userId ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;

  }

  @Override
  public void createCancellationRecord(
      CreateCancellationRecordRequestDto createCancellationRecordRequestDto) {
    String sql = "INSERT INTO cancellation_record (cancellation_record_id, order_id, user_id, cancel_reason, note, create_date, update_date, is_canceled) "
        + "VALUES (:cancellationRecordId, :orderId, :userId, :cancelReason, :note, :createDate, :updateDate, :isCanceled)";

    Map<String, Object> map = new HashMap<>();

    LocalDate now = LocalDate.now();
    map.put("cancellationRecordId", createCancellationRecordRequestDto.getCancellationRecordId());
    map.put("orderId", createCancellationRecordRequestDto.getOrderId());
    map.put("userId", createCancellationRecordRequestDto.getUserId());
    map.put("cancelReason", createCancellationRecordRequestDto.getCancelReason().name());
    map.put("note", createCancellationRecordRequestDto.getNote());
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("isCanceled", createCancellationRecordRequestDto.isCanceled());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public CancellationRecord getCancellationRecordById(String cancellationRecordId) {
    String sql = "SELECT cancellation_record_id, order_id, user_id, cancel_reason, note, create_date, update_date, is_canceled "
        + "FROM cancellation_record "
        + "WHERE cancellation_record_id = :cancellationRecordId ";

    Map<String, Object> map = new HashMap<>();
    map.put("cancellationRecordId", cancellationRecordId);


    List<CancellationRecord> cancellationRecordList = namedParameterJdbcTemplate.query(sql, map, new CancellationRecordRowMapper());

    if (cancellationRecordList.size() > 0) {
      return cancellationRecordList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public CancellationRecord getCancellationReocrdByOrderId(String orderId) {
    String sql = "SELECT cancellation_record_id, order_id, user_id, cancel_reason, note, create_date, update_date, is_canceled "
        + "FROM cancellation_record "
        + "WHERE order_id = :orderId ";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);


    List<CancellationRecord> cancellationRecordList = namedParameterJdbcTemplate.query(sql, map, new CancellationRecordRowMapper());

    if (cancellationRecordList.size() > 0) {
      return cancellationRecordList.get(0);
    } else {
      return null;
    }
  }
}
