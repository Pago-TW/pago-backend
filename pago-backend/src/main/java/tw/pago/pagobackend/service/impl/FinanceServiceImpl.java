package tw.pago.pagobackend.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.BankAccountDao;
import tw.pago.pagobackend.dao.BankBranchDao;
import tw.pago.pagobackend.dao.BankDao;
import tw.pago.pagobackend.dto.BankAccountResponseDto;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
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
    String userId = createBankAccountRequestDto.getUserId();

    createBankAccountRequestDto.setBankAccountId(bankAccountId);

    // If user have not created any bankAccount, set isDefault = true
    List<BankAccount> bankAccountList = bankAccountDao.getBankAccountListByUserId(userId);
    createBankAccountRequestDto.setIsDefault(bankAccountList.isEmpty());

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
    String districtChineseName = zipCodeUtil.getCityAndDistrictByZipCode(zipCode);

    Bank bank = bankDao.getBankByBankCode(bankCode);
    String bankChineseName = bank.getName();
    String bankLogoUrl = bank.getBankLogoUrl();

    BankBranch bankBranch = bankBranchDao.getBankBranchByBranchCode(branchCode);
    String bankBranchName = bankBranch.getBranchName();

    if (bankBranchName.startsWith(bankChineseName)) {
      bankBranchName = bankBranchName.replaceFirst(bankChineseName, "");
    }

    String branchAdministrativeDivision = bankBranch.getAdministrativeDivision();

    BankAccountResponseDto bankAccountResponseDto = modelMapper.map(bankAccount,
        BankAccountResponseDto.class);

    // Hide bank account number with " * "
    if (accountNumber.length() > 6) {
      String firstPart = accountNumber.substring(0, 4);
      String lastPart = accountNumber.substring(accountNumber.length() - 2);
      String replacement = String.join("", Collections.nCopies(accountNumber.length() - 6, "*"));
      String maskedAccountNumber = firstPart + replacement + lastPart;
      bankAccountResponseDto.setAccountNumber(maskedAccountNumber);
    } else {
      bankAccountResponseDto.setAccountNumber(accountNumber);
    }

    bankAccountResponseDto.setResidentialDistrict(districtChineseName);
    bankAccountResponseDto.setBankName(bankChineseName);
    bankAccountResponseDto.setBankLogoUrl(bankLogoUrl);
    bankAccountResponseDto.setBranchName(bankBranchName);
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

    List<BankAccountResponseDto> bankAccountResponseDtoList = bankAccountList.stream()
        .map(bankAccount -> getBankAccountResponseDtoByBankAccount(bankAccount)).collect(
            Collectors.toList());

    return bankAccountResponseDtoList;
  }

  @Override
  public List<Bank> getBankList() {
    return bankDao.getBankList();
  }

  @Override
  public List<BankBranch> getBankBranchListByAdministrativeDivisionAndBankCode(
      String administrativeDivision,
      String bankCode) {

    return bankBranchDao.getBankBranchListByAdministrativeDivisionAndBankCode(
        administrativeDivision, bankCode);
  }

  @Override
  @Transactional
  public void changeDefaultBankAccount(String bankAccountId, String userId) {
    // Find the current default account
    BankAccount originalDefaultBankAccount = bankAccountDao.getUserDefaultBankAccount(userId);
    if (originalDefaultBankAccount == null) {
      throw new ResourceNotFoundException("Original default Bank Account not found");
    }

    String originalDefaultBankAccountId = originalDefaultBankAccount.getBankAccountId();

    // Change default bank account
    bankAccountDao.updateBankAccountIsDefault(originalDefaultBankAccountId, false);
    bankAccountDao.updateBankAccountIsDefault(bankAccountId, true);

  }

  @Override
  public void deleteBankAccount(String bankAccountId) {

    bankAccountDao.deleteBankAccount(bankAccountId);
  }
}
