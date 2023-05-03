package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;


@JsonPropertyOrder({
    "orderId",
    "serialNumber",
    "consumerId",
    "destinationCountryName",
    "destinationCityName",
    "destinationCountryCode",
    "destinationCityCode",
    "latestReceiveItemDate",
    "note",
    "orderStatus",
    "orderItem",
    "travelerFee",
    "tariffFee",
    "platformFee",
    "totalAmount",
    "currency",
    "hasNewActivity",
    "isPackagingRequired",
    "isVerificationRequired",
    "createDate",
    "updateDate"
})
@Data
@NoArgsConstructor
public class OrderResponseDto {
  // Order
  private String orderId;

  @JsonIgnore
  private String orderItemId;
  private String consumerId;
  private String serialNumber;
  private Date createDate;
  private Date updateDate;
  @JsonProperty("isPackagingRequired")
  private Boolean isPackagingRequired;
  @JsonProperty("isVerificationRequired")
  private Boolean isVerificationRequired;
  private String destinationCountryName;
  @JsonProperty("destinationCountryCode")
  private CountryCode destinationCountry;
  private String destinationCityName;
  @JsonProperty("destinationCityCode")
  private CityCode destinationCity;
  private CurrencyEnum currency;
  @JsonIgnore
  private Double platformFeePercent;
  @JsonIgnore
  private Double tariffFeePercent;
  private Date latestReceiveItemDate;
  private String note;
  private OrderStatusEnum orderStatus;

  // OrderItem
  private OrderItemDto orderItem;

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
