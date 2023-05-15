package tw.pago.pagobackend.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.CreateBankAccountRequestDto;
import tw.pago.pagobackend.service.FinanceService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@AllArgsConstructor
@RestController
public class FinanceController {

  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final FinanceService financeService;

  @PostMapping("/bank-accounts")
  public ResponseEntity<?> createBankAccount(@RequestBody
      CreateBankAccountRequestDto createBankAccountRequestDto) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    createBankAccountRequestDto.setUserId(currentLoginUserId);

    financeService.createBankAccount(createBankAccountRequestDto);


    return ResponseEntity.status(HttpStatus.CREATED).body("");
  }



}
