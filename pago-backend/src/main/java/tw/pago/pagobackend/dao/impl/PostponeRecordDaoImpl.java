package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.PostponeRecordDao;
import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;

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



}
