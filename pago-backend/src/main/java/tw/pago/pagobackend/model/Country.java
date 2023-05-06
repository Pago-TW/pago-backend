package tw.pago.pagobackend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Country {
    private String countryCode;
    private String englishName;
    private String chineseName;

}
