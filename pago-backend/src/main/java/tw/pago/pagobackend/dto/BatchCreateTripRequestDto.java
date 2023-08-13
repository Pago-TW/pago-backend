package tw.pago.pagobackend.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;

@Data
@NoArgsConstructor
public class BatchCreateTripRequestDto {
  private String tripCollectionName;
  private Departure departure;
  private List<Stop> stops;
  private String shopperId;

  @Data
  public static class Departure {

    private CountryCode country;
    private CityCode city;
  }

  @Data
  public static class Stop {

    private CountryCode country;
    private CityCode city;
    private Date arrivalDate;
  }

}
