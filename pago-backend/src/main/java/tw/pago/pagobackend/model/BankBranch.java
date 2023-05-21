package tw.pago.pagobackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankBranch {
  private String bankCode;
  private String branchCode;
  private String branchName;
  private String address;
  private String phoneNumber;
  private String administrativeDivision;
}
