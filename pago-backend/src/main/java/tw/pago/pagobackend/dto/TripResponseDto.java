package tw.pago.pagobackend.dto;


import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.TripStatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDto {
  private String tripId;
  private String shopperId;
  private String fromCountry;
  private String fromCity;
  private String toCountry;
  private String toCity;
  private Date arrivalDate;
  private BigDecimal profit;
  private CurrencyEnum currency;
  private Date createDate;
  private Date updateDate;
  private TripStatusEnum tripStatus;
  private boolean hasNewActivity;
  private TripDashboardDto dashboard;

}
