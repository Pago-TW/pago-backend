package tw.pago.pagobackend.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@AllArgsConstructor
@RestController
public class FinanceController {

  private final CurrentUserInfoProvider currentUserInfoProvider;

  @PostMapping("/users/{userId}/bankAccount")
  public ResponseEntity<?> createBankAccount(@PathVariable String userId) {


    return ResponseEntity.status(HttpStatus.CREATED).body("");
  }



}
