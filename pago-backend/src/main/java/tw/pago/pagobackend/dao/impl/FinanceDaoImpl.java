package tw.pago.pagobackend.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.FinanceDao;

@Repository
@AllArgsConstructor
public class FinanceDaoImpl implements FinanceDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
}
