package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;

public interface FinanceDao {
  void createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto);

}
