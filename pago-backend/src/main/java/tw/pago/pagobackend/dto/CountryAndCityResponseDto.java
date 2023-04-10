package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.model.City;
import tw.pago.pagobackend.model.Country;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CountryAndCityResponseDto {
  private List<Country> countries;
  private List<City> cities;

}
