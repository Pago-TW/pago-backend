package tw.pago.pagobackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bank {
  private String bankCode;
  private String name;
  private String bankLogoUrl;
}
