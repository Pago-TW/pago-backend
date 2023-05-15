package tw.pago.pagobackend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BankAccount {

  String bankAccountId;
  String userId;
  String legalName;
  LocalDate birthDate;
  String identifyNumber;
  String residentialAddress;
  String bankName;
  String region;
  String branchName;
  String accountHolderName;
  String accountNumber;




}
