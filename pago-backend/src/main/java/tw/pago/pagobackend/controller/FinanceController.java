package tw.pago.pagobackend.controller;


import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.BankAccountResponseDto;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.model.Bank;
import tw.pago.pagobackend.model.BankAccount;
import tw.pago.pagobackend.model.BankBranch;
import tw.pago.pagobackend.service.FinanceService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@AllArgsConstructor
@RestController
public class FinanceController {

  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final FinanceService financeService;

  @PostMapping("/bank-accounts")
  public ResponseEntity<BankAccount> createBankAccount(@RequestBody
      CreateBankAccountRequestDto createBankAccountRequestDto) {
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
  public ResponseEntity<?> getBankAccountList() {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    List<BankAccount> bankAccountList = financeService.getBankAccountListByUserId(currentLoginUserId);


    return ResponseEntity.status(HttpStatus.OK).body(bankAccountList);
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
}
