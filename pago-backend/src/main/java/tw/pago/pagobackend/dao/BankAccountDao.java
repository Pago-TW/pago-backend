package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.BankAccount;

public interface BankAccountDao {
  void createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto);

  BankAccount getBankAccountById(String bankAccountId);

  List<BankAccount> getBankAccountListByUserId(String userId);

}
