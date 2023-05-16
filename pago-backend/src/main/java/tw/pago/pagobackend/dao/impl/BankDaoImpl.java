package tw.pago.pagobackend.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.BankDao;
import tw.pago.pagobackend.model.Bank;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.rowmapper.BankAccountRowMapper;
import tw.pago.pagobackend.rowmapper.BankRowMapper;

@Repository
@AllArgsConstructor
public class BankDaoImpl implements BankDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public List<Bank> getBankList() {
    String sql = "SELECT bank_code, name "
        + "FROM bank ";

    Map<String, Object> map = new HashMap<>();

    List<Bank> bankList = namedParameterJdbcTemplate.query(sql, map, new BankRowMapper());

      return bankList;
  }
}
