package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neovisionaries.i18n.CountryCode;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;

@Data
@NoArgsConstructor
public class CreateTripRequestDto {

  private String tripId;

  @NotNull
  private CountryCode fromCountry;

  @NotNull
  private CountryCode toCountry;

  @NotNull
  private CityCode fromCity;

  @NotNull
  private CityCode toCity;

  @NotNull
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date arrivalDate;

}
