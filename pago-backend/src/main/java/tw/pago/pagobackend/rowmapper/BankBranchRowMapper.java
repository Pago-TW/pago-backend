package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.BankBranch;

public class BankBranchRowMapper implements RowMapper<BankBranch> {

  @Override
  public BankBranch mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    BankBranch bankBranch = new BankBranch();
    bankBranch.setBankCode(resultSet.getString("bank_code"));
    bankBranch.setBranchCode(resultSet.getString("branch_code"));
    bankBranch.setBranchName(resultSet.getString("branch_name"));
    bankBranch.setAddress(resultSet.getString("address"));
    bankBranch.setPhoneNumber(resultSet.getString("phone_number"));
    bankBranch.setAdministrativeDivision(resultSet.getString("administrative_division"));


    return bankBranch;
  }
}
