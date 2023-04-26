package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;

@Getter
@Setter
@Builder
public class UpdateBidRequestDto {

  private String bidId;
  private String orderId;
  private String tripId;
  private BigDecimal bidAmount;
  private CurrencyEnum currency;
  private BidStatusEnum bidStatus;
  private Date latestDeliveryDate;



}
