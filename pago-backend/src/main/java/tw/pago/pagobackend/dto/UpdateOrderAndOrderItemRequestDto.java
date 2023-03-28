package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
// import tw.pago.pagobackend.constant.PackagingEnum;

@Getter
@Setter
@Builder
public class UpdateOrderAndOrderItemRequestDto {


  private String orderId;

  private String consumerId;

  @JsonProperty(value = "orderItem")
  private Optional<UpdateOrderItemDto> updateOrderItemDto;


  private boolean isPackagingRequired;


  private boolean isVerificationRequired;



  private CountryCode destinationCountry;

  private CityCode destinationCity;


  private BigDecimal travelerFee;


  private CurrencyEnum currency;

  @Value("4.5")
  private Double platformFeePercent;

  @Value("2.5")
  private Double tariffFeePercent;


  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date latestReceiveItemDate;

  private String note;

  private OrderStatusEnum orderStatus;


}
