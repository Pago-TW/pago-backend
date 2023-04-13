package tw.pago.pagobackend.model;

import com.neovisionaries.i18n.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Country {
    private CountryCode countryCode;
    private String englishName;
    private String chineseName;

}
