package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankAccountResponseDto {
  private String bankAccountId;
  private String userId;
  private String legalName;
  private LocalDate birthDate;
  private String zipCode;
  private String bankCode;
  private String bankName;
  private String bankLogoUrl;
  private String branchAdministrativeDivision;
  private String branchCode;
  private String branchName;
  private String accountHolderName;
  private String accountNumber;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
  private Boolean isDefault;
}
