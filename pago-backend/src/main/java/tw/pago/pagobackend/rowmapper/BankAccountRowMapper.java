package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.BankAccount;

public class BankAccountRowMapper implements RowMapper<BankAccount> {

  @Override
  public BankAccount mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    BankAccount bankAccount = new BankAccount();
    bankAccount.setBankAccountId(resultSet.getString("bank_account_id"));
    bankAccount.setUserId(resultSet.getString("user_id"));
    bankAccount.setLegalName(resultSet.getString("legal_name"));
    bankAccount.setBirthDate(resultSet.getDate("birth_date").toLocalDate());
    bankAccount.setZipCode(resultSet.getString("zip_code"));
    bankAccount.setBankCode(resultSet.getString("bank_code"));
    bankAccount.setBranchCode(resultSet.getString("branch_code"));
    bankAccount.setAccountHolderName(resultSet.getString("account_holder_name"));
    bankAccount.setAccountNumber(resultSet.getString("account_number"));
    bankAccount.setIsDefault(resultSet.getBoolean("is_default"));
    bankAccount.setCreateDate(resultSet.getTimestamp("create_date").toInstant().atZone(ZoneId.of("UTC")));
    bankAccount.setUpdateDate(resultSet.getTimestamp("update_date").toInstant().atZone(ZoneId.of("UTC")));

    return bankAccount;
  }
}
