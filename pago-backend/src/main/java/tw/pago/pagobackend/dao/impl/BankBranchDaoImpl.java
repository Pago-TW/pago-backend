package tw.pago.pagobackend.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.BankBranchDao;
import tw.pago.pagobackend.model.BankBranch;
import tw.pago.pagobackend.rowmapper.BankBranchRowMapper;

@Repository
@AllArgsConstructor
public class BankBranchDaoImpl implements BankBranchDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public List<BankBranch> getBankBranchListByAdministrativeDivisionAndBankCode(String administrativeDivision,
      String bankCode) {
    // Because other data is no need, front-end only need branchCode and branchName
    String sql = "SELECT * "
        + "FROM bank_branch "
        + "WHERE administrative_division = :administrativeDivision AND bank_code = :bankCode ";

    Map<String, Object> map = new HashMap<>();
    map.put("administrativeDivision", administrativeDivision);
    map.put("bankCode", bankCode);

    List<BankBranch> bankBranchList = namedParameterJdbcTemplate.query(sql, map,
        new BankBranchRowMapper());

    return bankBranchList;
  }
}
