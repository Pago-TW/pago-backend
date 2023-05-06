package tw.pago.pagobackend.dto;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;

@Data
@NoArgsConstructor
public class MatchingTripForOrderDto {

  private String tripId;
  private CityCode fromCity;
  private CityCode toCity;
  private Date arrivalDate;
  private Date createDate;
  private Date updateDate;

}
