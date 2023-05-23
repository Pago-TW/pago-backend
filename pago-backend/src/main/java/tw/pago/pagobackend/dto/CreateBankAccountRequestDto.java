package tw.pago.pagobackend.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBankAccountRequestDto {
  private String bankAccountId;
  private String userId;
  @NotBlank
  private String legalName;
  private LocalDate birthDate; // TODO 跟前端討論會傳入的 date pattern
  @NotBlank
  private String zipCode;
  @NotBlank
  private String bankCode;
  @NotBlank
  private String branchCode;
  @NotBlank
  private String accountHolderName;
  @NotBlank
  private String accountNumber;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
  private Boolean isDefault;

}
