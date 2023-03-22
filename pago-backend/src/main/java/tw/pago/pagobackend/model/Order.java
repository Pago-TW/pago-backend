package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;

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
  @JsonProperty("isPackagingRequired")
  private boolean isPackagingRequired;
  @JsonProperty("isVerificationRequired")
  private boolean isVerificationRequired;
  private String destination;
  private CurrencyEnum currency;
  @JsonIgnore
  private Double platformFeePercent;
  @JsonIgnore
  private Double tariffFeePercent;
  private Date latestReceiveItemDate;
  private String note;
  private OrderStatusEnum orderStatus;

  // OrderItem
  private OrderItem orderItem;

  // For programing use
  private BigDecimal travelerFee;
  private BigDecimal tariffFee;
  private BigDecimal platformFee;
  private BigDecimal totalAmount;
  private boolean hasNewActivity;

}
