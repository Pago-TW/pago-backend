package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.BankAccount;

public interface FinanceService {
  BankAccount createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto);

}
