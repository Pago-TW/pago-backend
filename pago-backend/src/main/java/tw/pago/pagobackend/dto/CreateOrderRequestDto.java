package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;

@Data
@NoArgsConstructor
public class CreateOrderRequestDto {

  @NotNull
  @JsonProperty(value = "orderItem")
  private CreateOrderItemDto createOrderItemDto;

  private String orderId;
  private String serialNumber;

  @NotNull
  private boolean packaging;

  @NotNull
  private boolean verification;

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
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date latestReceiveItemDate;

  private String note;

  // @Value("REQUESTED")
  private OrderStatusEnum orderStatus;

}
