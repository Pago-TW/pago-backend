package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.dto.BankAccountResponseDto;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.Bank;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.model.BankBranch;

public interface FinanceService {
  BankAccount createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto);

  BankAccount getBankAccountById(String bankAccountId);

  BankAccountResponseDto getBankAccountResponseDtoByBankAccount(BankAccount bankAccount);

  List<BankAccount> getBankAccountListByUserId(String userId);

  List<BankAccountResponseDto> getBankAccountResponseDtoListByBankAccountList(List<BankAccount> bankAccountList);

  List<Bank> getBankList();

  List<BankBranch> getBankBranchListByAdministrativeDivisionAndBankCode(String administrativeDivision, String bankCode);

  void changeDefaultBankAccount(String bankAccountId, String userId);

  void deleteBankAccount(String bankAccountId);

}
