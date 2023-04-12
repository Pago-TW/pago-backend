package tw.pago.pagobackend.dao.impl;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.CancellationRecordDao;

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
}
