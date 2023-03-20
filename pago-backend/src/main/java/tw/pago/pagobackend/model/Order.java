package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
// import tw.pago.pagobackend.constant.PackagingEnum;

@Getter
@Setter
@Builder
public class Order {

  // Order
  private String orderId;

  @JsonIgnore
  private String orderItemId;

  private String consumerId;
  private Date createDate;
  private Date updateDate;
  private boolean isPackagingRequired;
  private boolean isVerificationRequired;
  private String destination;
  private BigDecimal travelerFee;
  private CurrencyEnum currency;
  private Double platformFeePercent;
  private Double tariffFeePercent;
  private Date latestReceiveItemDate;
  private String note;
  private OrderStatusEnum orderStatus;

  // OrderItem
  private OrderItem orderItem;


}
