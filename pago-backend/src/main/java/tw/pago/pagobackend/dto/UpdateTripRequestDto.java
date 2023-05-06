package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;


@Data
@NoArgsConstructor
public class UpdateTripRequestDto {


  private String tripId;

  private String shopperId;

  private CountryCode fromCountry;

  private CountryCode toCountry;

  private CityCode fromCity;

  private CityCode toCity;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date arrivalDate;

  private BigDecimal profit;
  private Date createDate;
  private Date updateDate;

}
