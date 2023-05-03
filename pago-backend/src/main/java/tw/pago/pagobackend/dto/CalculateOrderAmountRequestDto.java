package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;

@Data
@NoArgsConstructor
public class CalculateOrderAmountRequestDto {
  // Calculate chosenBid
  private String bidId;

  // Calculate order amount during creation
  @JsonProperty(value = "orderItem")
  private CreateOrderItemDto createOrderItemDto;
  private String orderId;
  private String serialNumber;
  private Boolean isPackagingRequired;
  private Boolean isVerificationRequired;
  private CountryCode destinationCountry;
  private CityCode destinationCity;
  private BigDecimal travelerFee;
  private CurrencyEnum currency;
  private Double platformFeePercent;
  private Double tariffFeePercent;
  @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date latestReceiveItemDate;
  private String note;
  private OrderStatusEnum orderStatus;

}

