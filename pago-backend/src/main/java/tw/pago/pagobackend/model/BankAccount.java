package tw.pago.pagobackend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BankAccount {

  private String bankAccountId;
  private String userId;
  private String legalName;
  private LocalDate birthDate;
  private String identifyNumber;
  private String residentialAddress;
  private String bankName;
  private String bankLocation;
  private String branchName;
  private String accountHolderName;
  private String accountNumber;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
  private Boolean isDefault;




}
