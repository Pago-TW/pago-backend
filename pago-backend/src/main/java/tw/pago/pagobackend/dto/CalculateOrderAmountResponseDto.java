package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CurrencyEnum;


@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CalculateOrderAmountResponseDto {
  private BidCreatorDto bidder;
  private BigDecimal travelerFee;
  private BigDecimal tariffFee;
  private BigDecimal platformFee;
  private BigDecimal totalAmount;
  private CurrencyEnum currency;

}
