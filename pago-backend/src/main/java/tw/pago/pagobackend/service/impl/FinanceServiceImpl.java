package tw.pago.pagobackend.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.BankAccountDao;
import tw.pago.pagobackend.dao.BankDao;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.Bank;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.service.FinanceService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class FinanceServiceImpl implements FinanceService {

  private final BankAccountDao bankAccountDao;
  private final BankDao bankDao;
  private final UuidGenerator uuidGenerator;

  @Override
  public BankAccount createBankAccount(CreateBankAccountRequestDto createBankAccountRequestDto) {
    String bankAccountId = uuidGenerator.getUuid();
    createBankAccountRequestDto.setBankAccountId(bankAccountId);

    bankAccountDao.createBankAccount(createBankAccountRequestDto);
    BankAccount bankAccount = getBankAccountById(bankAccountId);
    return bankAccount;
  }

  @Override
  public BankAccount getBankAccountById(String bankAccountId) {
    return bankAccountDao.getBankAccountById(bankAccountId);
  }

  @Override
  public List<Bank> getBankList() {
    return bankDao.getBankList();
  }
}
