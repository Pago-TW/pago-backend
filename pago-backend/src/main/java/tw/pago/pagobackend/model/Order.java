package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.OrderChosenShopperDto;

@Getter
@Setter
@Builder
public class Order {

  // Order
  private String orderId;
  private String serialNumber;

  @JsonIgnore
  private String orderItemId;

  private String consumerId;
  private Date createDate;
  private Date updateDate;
  @JsonProperty("isPackagingRequired")
  private boolean isPackagingRequired;
  @JsonProperty("isVerificationRequired")
  private boolean isVerificationRequired;
  private CountryCode destinationCountry;
  private CityCode destinationCity;
  private CurrencyEnum currency;
  @JsonIgnore
  private Double platformFeePercent;
  @JsonIgnore
  private Double tariffFeePercent;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
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
  private OrderChosenShopperDto shopper;

  @JsonProperty("isApplicant")
  private Boolean isApplicant;
  @JsonProperty("isBidder")
  private boolean isBidder;
  private boolean hasNewActivity;

}
