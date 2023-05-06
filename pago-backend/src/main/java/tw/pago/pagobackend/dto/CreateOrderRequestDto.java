package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.validation.NotBeforeToday;

@Data
@NoArgsConstructor
public class CreateOrderRequestDto {

  @NotNull
  @JsonProperty(value = "orderItem")
  private CreateOrderItemDto createOrderItemDto;

  private String orderId;
  private String serialNumber;


  @NotNull
  @JsonProperty(value = "isPackagingRequired")
  private Boolean isPackagingRequired;

  @NotNull
  @JsonProperty(value = "isVerificationRequired")
  private Boolean isVerificationRequired;
  @NotNull
  private CountryCode destinationCountry;
  @NotNull
  private CityCode destinationCity;
  @NotNull
  private BigDecimal travelerFee;
  @NotNull
  private CurrencyEnum currency;

  // @Value("4.5")
  private Double platformFeePercent;

  // @Value("2.5")
  private Double tariffFeePercent;

  @NotNull
  @NotBeforeToday
  @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date latestReceiveItemDate;

  private String note;

  // @Value("REQUESTED")
  private OrderStatusEnum orderStatus;

}
