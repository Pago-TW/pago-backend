package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.Bank;

public class BankRowMapper implements RowMapper<Bank> {


  @Override
  public Bank mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Bank bank = new Bank();
    bank.setBankCode(resultSet.getString("bank_code"));
    bank.setName(resultSet.getString("name"));
    bank.setBankLogoUrl(resultSet.getString("bank_logo_url"));

    return bank;
  }
}
