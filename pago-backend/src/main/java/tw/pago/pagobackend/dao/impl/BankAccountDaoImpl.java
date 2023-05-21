package tw.pago.pagobackend.dao.impl;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.BankAccountDao;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.rowmapper.BankAccountRowMapper;

@Repository
@AllArgsConstructor
public class BankAccountDaoImpl implements BankAccountDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto) {
    String sql = "INSERT INTO bank_account (bank_account_id, user_id, legal_name, birth_date, "
        + "identity_number, residential_address, zip_code, bank_code, branch_code, "
        + "account_holder_name, account_number, is_default, create_date, update_date) "
        + "VALUES (:bankAccountId, :userId, :legalName, :birthDate, :identityNumber, "
        + ":residentialAddress, :zipCode, :bankCode, :branchCode, :accountHolderName, "
        + ":accountNumber, :isDefault, :createDate, :updateDate )";

    Map<String, Object> map = new HashMap<>();

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

    map.put("bankAccountId", createBankAccountRequestDto.getBankAccountId());
    map.put("userId", createBankAccountRequestDto.getUserId());
    map.put("legalName", createBankAccountRequestDto.getLegalName());
    map.put("birthDate", createBankAccountRequestDto.getBirthDate());
    map.put("identityNumber", createBankAccountRequestDto.getIdentityNumber());
    map.put("residentialAddress", createBankAccountRequestDto.getResidentialAddress());
    map.put("zipCode", createBankAccountRequestDto.getZipCode());
    map.put("bankCode", createBankAccountRequestDto.getBankCode());
    map.put("branchCode", createBankAccountRequestDto.getBranchCode());
    map.put("accountHolderName", createBankAccountRequestDto.getAccountHolderName());
    map.put("accountNumber", createBankAccountRequestDto.getAccountNumber());
    map.put("isDefault", createBankAccountRequestDto.getIsDefault());
    map.put("createDate", Timestamp.from(now.toInstant()));
    map.put("updateDate", Timestamp.from(now.toInstant()));

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public BankAccount getBankAccountById(String bankAccountId) {
    String sql = "SELECT bank_account_id, user_id, legal_name, birth_date, "
        + "identity_number, residential_address, zip_code,bank_code, branch_code, "
        + "account_holder_name, account_number, is_default, create_date, update_date "
        + "FROM bank_account "
        + "WHERE bank_account_id = :bankAccountId ";

    Map<String, Object> map = new HashMap<>();
    map.put("bankAccountId", bankAccountId);


    List<BankAccount> bankAccountList = namedParameterJdbcTemplate.query(sql, map, new BankAccountRowMapper());

    if (bankAccountList.size() > 0) {
      return bankAccountList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<BankAccount> getBankAccountListByUserId(String userId) {
    String sql = "SELECT bank_account_id, user_id, legal_name, birth_date, "
        + "identity_number, residential_address, zip_code,bank_code, branch_code, "
        + "account_holder_name, account_number, is_default, create_date, update_date "
        + "FROM bank_account "
        + "WHERE user_id = :userId ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);


    List<BankAccount> bankAccountList = namedParameterJdbcTemplate.query(sql, map, new BankAccountRowMapper());


    return bankAccountList;
  }

}
