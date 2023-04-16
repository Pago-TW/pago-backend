package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CurrencyEnum;


@Data
@NoArgsConstructor
public class CalculateOrderAmountResponseDto {
  private BigDecimal travelerFee;
  private BigDecimal tariffFee;
  private BigDecimal platformFee;
  private BigDecimal totalAmount;
  private CurrencyEnum currency;

}
