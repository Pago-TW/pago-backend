package tw.pago.pagobackend.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.NotificationDao;

@Repository
@AllArgsConstructor
public class NotificationDaoImpl implements NotificationDao {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

}
