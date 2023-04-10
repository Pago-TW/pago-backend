package tw.pago.pagobackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;

@RestController
public class CountryCodeController {

    @GetMapping("/countries")
    public List<String> getCountries() {
        List<String> countries = new ArrayList<>();
        for (CountryCode countryCode : CountryCode.values()) {
            countries.add(countryCode.getAlpha2());
        }
        return countries;
    }
}
