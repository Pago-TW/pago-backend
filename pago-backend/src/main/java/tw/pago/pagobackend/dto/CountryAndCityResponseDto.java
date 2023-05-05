package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.model.City;
import tw.pago.pagobackend.model.Country;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CountryAndCityResponseDto {
  private Country country;
  private City city;
}
