package tw.pago.pagobackend.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.DailyCountDao;

@Repository
@AllArgsConstructor
public class DailyCountDaoImpl implements DailyCountDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void updateSmsCount(String userId) {

  }

  @Override
  public void updateEmailCount(String userId) {

  }
}
