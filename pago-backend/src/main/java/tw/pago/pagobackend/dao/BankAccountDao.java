package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.BankAccount;

public interface BankAccountDao {
  void createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto);

  BankAccount getBankAccountById(String bankAccountId);

  BankAccount getUserDefaultBankAccount(String userId);

  List<BankAccount> getBankAccountListByUserId(String userId);

  void updateBankAccountIsDefault(String bankAccountId, boolean isDefault);

  void deleteBankAccount(String bankAccountId);

}
