package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TransactionYearMonthRowMapper implements RowMapper<String> {
  @Override
  public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return resultSet.getString("transaction_ym");
  }
}

