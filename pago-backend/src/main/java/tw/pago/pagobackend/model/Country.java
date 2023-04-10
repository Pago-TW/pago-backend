package tw.pago.pagobackend.model;

import com.neovisionaries.i18n.CountryCode;
public class Country {
    private CountryCode code;
    private String name;

    public Country(CountryCode code, String name) {
        this.code = code;
        this.name = name;
    }

    public CountryCode getCode() {
        return code;
    }

    public void setCode(CountryCode code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
