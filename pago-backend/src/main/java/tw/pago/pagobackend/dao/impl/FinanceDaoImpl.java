package tw.pago.pagobackend.dao.impl;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.FinanceDao;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;

@Repository
@AllArgsConstructor
public class FinanceDaoImpl implements FinanceDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto) {
    String sql = "INSERT INTO bank_account (bank_account_id, user_id, legal_name, birth_date, "
        + "identity_number, residential_address, bank_name, bank_location, branch_name, "
        + "account_holder_name, account_number, create_date, update_date) "
        + "VALUES (:bankAccountId, :userId, :legalName, :birthDate, :identityNumber, "
        + ":residentialAddress, :bankName, :bankLocation, :branchName, :accountHolderName, "
        + ":accountNumber, :createDate, :updateDate )";

    Map<String, Object> map = new HashMap<>();

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

    map.put("bankAccountId", createBankAccountRequestDto.getBankAccountId());
    map.put("userId", createBankAccountRequestDto.getUserId());
    map.put("legalName", createBankAccountRequestDto.getLegalName());
    map.put("birthDate", createBankAccountRequestDto.getBirthDate());
    map.put("identityNumber", createBankAccountRequestDto.getIdentifyNumber());
    map.put("residentialAddress", createBankAccountRequestDto.getResidentialAddress());
    map.put("bankName", createBankAccountRequestDto.getBankName());
    map.put("bankLocation", createBankAccountRequestDto.getBankLocation());
    map.put("branchName", createBankAccountRequestDto.getBranchName());
    map.put("accountHolderName", createBankAccountRequestDto.getAccountHolderName());
    map.put("accountNumber", createBankAccountRequestDto.getAccountNumber());
    map.put("createDate", Timestamp.from(now.toInstant()));
    map.put("updateDate", Timestamp.from(now.toInstant()));

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }
}
