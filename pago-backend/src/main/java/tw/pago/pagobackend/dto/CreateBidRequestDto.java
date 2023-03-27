package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;


@Getter
@Setter
@Builder
public class CreateBidRequestDto {

  private String bidId;

  private String orderId;

  private String tripId;

  @NotNull
  private BigDecimal bidAmount;

  private CurrencyEnum currency;

  @NotNull
  @JsonFormat(pattern="yyyy-MM-dd")
  private Date latestDeliveryDate;

  private BidStatusEnum bidStatus;


}
