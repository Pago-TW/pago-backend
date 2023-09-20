package tw.pago.pagobackend.controller;


import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.BankAccountResponseDto;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.dto.OtpValidationDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.model.Bank;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.model.BankBranch;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.FinanceService;
import tw.pago.pagobackend.service.OtpService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@AllArgsConstructor
@RestController
@Validated
public class FinanceController {

  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final FinanceService financeService;
  private final OtpService otpService;

  @PostMapping("/bank-accounts") // TODO 檢查一下是否用戶近期有驗證手機過
  public ResponseEntity<BankAccount> createBankAccount(@RequestBody
      @Valid CreateBankAccountRequestDto createBankAccountRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    createBankAccountRequestDto.setUserId(currentLoginUserId);

    BankAccount bankAccount = financeService.createBankAccount(createBankAccountRequestDto);


    return ResponseEntity.status(HttpStatus.CREATED).body(bankAccount);
  }

  @GetMapping("/bank-accounts/{bankAccountId}")
  public ResponseEntity<BankAccountResponseDto> getBankAccount(@PathVariable String bankAccountId) {
    BankAccount bankAccount = financeService.getBankAccountById(bankAccountId);
    BankAccountResponseDto bankAccountResponseDto = financeService.getBankAccountResponseDtoByBankAccount(bankAccount);

    return ResponseEntity.status(HttpStatus.OK).body(bankAccountResponseDto);
  }

  @GetMapping("bank-accounts")
  public ResponseEntity<List<BankAccountResponseDto>> getBankAccountList() {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    List<BankAccount> bankAccountList = financeService.getBankAccountListByUserId(currentLoginUserId);
    List<BankAccountResponseDto> bankAccountResponseDtoList = financeService.getBankAccountResponseDtoListByBankAccountList(bankAccountList);


    return ResponseEntity.status(HttpStatus.OK).body(bankAccountResponseDtoList);
  }


  @GetMapping("/banks")
  public ResponseEntity<List<Bank>> getBankList() {
    List<Bank> bankList = financeService.getBankList();

    return ResponseEntity.status(HttpStatus.OK).body(bankList);
  }

  @GetMapping("/bank-branches")
  public ResponseEntity<List<BankBranch>> getBankBranchList(
      @RequestParam(required = true) String administrativeDivision,
      @RequestParam(required = true) String bankCode) {
    List<BankBranch> bankBranchList = financeService.getBankBranchListByAdministrativeDivisionAndBankCode(administrativeDivision, bankCode);

    return ResponseEntity.status(HttpStatus.OK).body(bankBranchList);
  }

  @PatchMapping("/bank-accounts/{bankAccountId}/default")
  public ResponseEntity<?> changeDefaultBankAccount(@PathVariable String bankAccountId, @RequestBody OtpValidationDto otpValidationDto) {

    // Permission checking
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    BankAccount bankAccount = financeService.getBankAccountById(bankAccountId);
    if (!bankAccount.getUserId().equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    // Otp validation
    User user = currentUserInfoProvider.getCurrentLoginUser();
    String phone = user.getPhone();
    String otpCode = otpValidationDto.getOtpCode();
    ValidatePhoneRequestDto validatePhoneRequestDto = new ValidatePhoneRequestDto();
    validatePhoneRequestDto.setPhone(phone);
    validatePhoneRequestDto.setOtpCode(otpCode);
    boolean isOtpValid = otpService.validateOtp(validatePhoneRequestDto);

    if (!isOtpValid) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP expired");
    }

    // Change default bank account
    financeService.changeDefaultBankAccount(bankAccountId, currentLoginUserId);
    return ResponseEntity.status(HttpStatus.OK).body("Change default bank account successfully");
  }


  @DeleteMapping("/bank-accounts/{bankAccountId}")
  public ResponseEntity<?> deleteBankAccount(@PathVariable String bankAccountId) {
    // Permission checking
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    BankAccount bankAccount = financeService.getBankAccountById(bankAccountId);
    String bankAccountCreatorId = bankAccount.getUserId();
    boolean isDefault = bankAccount.getIsDefault();

    if (!bankAccountCreatorId.equals(currentLoginUserId) || isDefault) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    financeService.deleteBankAccount(bankAccountId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
