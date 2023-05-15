package tw.pago.pagobackend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.FinanceDao;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.service.FinanceService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class FinanceServiceImpl implements FinanceService {

  private final FinanceDao financeDao;
  private final UuidGenerator uuidGenerator;

  @Override
  public BankAccount createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto) {
    String bankAccountId = uuidGenerator.getUuid();
    createBankAccountRequestDto.setBankAccountId(bankAccountId);

    financeDao.createBankAccount(createBankAccountRequestDto);
    return null;
  }
}
