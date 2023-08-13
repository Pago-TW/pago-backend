package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;

@Data
@NoArgsConstructor
public class CreateTripRequestDto {

  private String tripId;
  private String shopperId;
  private String tripCollectionId;

  @NotNull
  private CountryCode fromCountry;

  @NotNull
  private CountryCode toCountry;

  @NotNull
  private CityCode fromCity;

  @NotNull
  private CityCode toCity;

  @NotNull
  @JsonFormat(pattern="yyyy-MM-dd")
  private Date arrivalDate;

}
