package tw.pago.pagobackend.model;


import com.neovisionaries.i18n.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {
  private String cityCode;
  private String englishName;
  private String chineseName;
  private CountryCode countryCode;

}
