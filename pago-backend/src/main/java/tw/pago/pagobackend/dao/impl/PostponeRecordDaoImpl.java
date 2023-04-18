package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.PostponeRecordDao;
import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;
import tw.pago.pagobackend.dto.UpdatePostponeRecordRequestDto;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.PostponeRecord;
import tw.pago.pagobackend.rowmapper.CancellationRecordRowMapper;
import tw.pago.pagobackend.rowmapper.PostponeRecordRowMapper;

@Repository
@AllArgsConstructor
public class PostponeRecordDaoImpl implements PostponeRecordDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Override
  public void createPostponeRecord(CreatePostponeRecordRequestDto createPostponeRecordRequestDto) {
    String sql = "INSERT INTO postpone_record (postpone_record_id, order_id, user_id, postpone_reason, note, create_date, update_date, is_postponed) "
        + "VALUES (:postponeRecordId, :orderId, :userId, :postponeReason, :note, :createDate, :updateDate, :isPostponed)";

    Map<String, Object> map = new HashMap<>();

    LocalDate now = LocalDate.now();
    map.put("postponeRecordId", createPostponeRecordRequestDto.getPostponeRecordId());
    map.put("orderId", createPostponeRecordRequestDto.getOrderId());
    map.put("userId", createPostponeRecordRequestDto.getUserId());
    map.put("postponeReason", createPostponeRecordRequestDto.getPostponeReason().name());
    map.put("note", createPostponeRecordRequestDto.getNote());
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("isPostponed", createPostponeRecordRequestDto.getIsPostponed());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }


  @Override
  public PostponeRecord getPostponeRecordById(String postponeRecordId) {
    String sql = "SELECT postpone_record_id, order_id, user_id, postpone_reason, note, create_date, update_date, is_postponed "
        + "FROM postpone_record "
        + "WHERE postpone_record_id = :postponeRecordId ";

    Map<String, Object> map = new HashMap<>();
    map.put("postponeRecordId", postponeRecordId);


    List<PostponeRecord> postponeRecordList = namedParameterJdbcTemplate.query(sql, map, new PostponeRecordRowMapper());

    if (postponeRecordList.size() > 0) {
      return postponeRecordList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public PostponeRecord getPostponeRecordByOrderId(String orderId) {
    String sql = "SELECT postpone_record_id, order_id, user_id, postpone_reason, note, create_date, update_date, is_postponed "
        + "FROM postpone_record "
        + "WHERE order_id = :orderId ";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);


    List<PostponeRecord> postponeRecordList = namedParameterJdbcTemplate.query(sql, map, new PostponeRecordRowMapper());

    if (postponeRecordList.size() > 0) {
      return postponeRecordList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void updatePostponeRecord(UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto) {
    String sql = "UPDATE postpone_record "
        + "SET is_postponed = :isPostponed, "
        + "    update_date = :updateDate "
        + "WHERE order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    LocalDate now = LocalDate.now();
    map.put("isPostponed", updatePostponeRecordRequestDto.getIsPostponed());
    map.put("updateDate", now);
    map.put("orderId", updatePostponeRecordRequestDto.getOrderId());

    namedParameterJdbcTemplate.update(sql, map);
  }
}
