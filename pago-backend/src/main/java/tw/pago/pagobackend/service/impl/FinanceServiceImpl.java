package tw.pago.pagobackend.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.BankAccountDao;
import tw.pago.pagobackend.dao.BankBranchDao;
import tw.pago.pagobackend.dao.BankDao;
import tw.pago.pagobackend.dto.BankAccountResponseDto;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.Bank;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.model.BankBranch;
import tw.pago.pagobackend.service.FinanceService;
import tw.pago.pagobackend.util.UuidGenerator;
import tw.pago.pagobackend.util.ZipCodeUtil;

@Service
@AllArgsConstructor
public class FinanceServiceImpl implements FinanceService {

  private final BankDao bankDao;
  private final BankBranchDao bankBranchDao;
  private final BankAccountDao bankAccountDao;
  private final UuidGenerator uuidGenerator;
  private final ZipCodeUtil zipCodeUtil;
  private final ModelMapper modelMapper;

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
  public BankAccountResponseDto getBankAccountResponseDtoByBankAccount(BankAccount bankAccount) {
    String accountNumber = bankAccount.getAccountNumber();
    String bankCode = bankAccount.getBankCode();
    String branchCode = bankAccount.getBranchCode();
    String zipCode = bankAccount.getZipCode();
    String residentialAddress = bankAccount.getResidentialAddress();
    String districtChineseName = zipCodeUtil.getCityAndDistrictByZipCode(zipCode);
    // if starts with district, replace district with district + "/"

    Bank bank = bankDao.getBankByBankCode(bankCode);
    String bankChineseName = bank.getName();
    String bankLogoUrl = bank.getBankLogoUrl();

    BankBranch bankBranch = bankBranchDao.getBankBranchByBranchCode(branchCode);
    String bankBranchName = bankBranch.getBranchName();

    if (bankBranchName.startsWith(bankChineseName)) {
      bankBranchName = bankBranchName.replaceFirst(bankChineseName, "");
    }

    String branchAdministrativeDivision = bankBranch.getAdministrativeDivision();

    BankAccountResponseDto bankAccountResponseDto = modelMapper.map(bankAccount, BankAccountResponseDto.class);



    bankAccountResponseDto.setBankName(bankChineseName);
    bankAccountResponseDto.setBankLogoUrl(bankLogoUrl);
    bankAccountResponseDto.setBranchName(bankBranchName);
    bankAccountResponseDto.setResidentialAddress(residentialAddress);
    bankAccountResponseDto.setBranchAdministrativeDivision(branchAdministrativeDivision);



    return bankAccountResponseDto;
  }

  @Override
  public List<BankAccount> getBankAccountListByUserId(String userId) {
    return bankAccountDao.getBankAccountListByUserId(userId);
  }

  @Override
  public List<BankAccountResponseDto> getBankAccountResponseDtoListByBankAccountList(
      List<BankAccount> bankAccountList) {

    List<BankAccountResponseDto> bankAccountResponseDtoList = bankAccountList.stream().map(bankAccount -> getBankAccountResponseDtoByBankAccount(bankAccount)).collect(
        Collectors.toList());

    return bankAccountResponseDtoList;
  }

  @Override
  public List<Bank> getBankList() {
    return bankDao.getBankList();
  }

  @Override
  public List<BankBranch> getBankBranchListByAdministrativeDivisionAndBankCode(String administrativeDivision,
      String bankCode) {

    return bankBranchDao.getBankBranchListByAdministrativeDivisionAndBankCode(administrativeDivision, bankCode);
  }
}
