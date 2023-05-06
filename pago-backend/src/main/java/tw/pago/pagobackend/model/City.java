package tw.pago.pagobackend.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.CountryCode;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class City {
  private String cityCode;
  private String englishName;
  private String chineseName;
  private CountryCode countryCode;

}
