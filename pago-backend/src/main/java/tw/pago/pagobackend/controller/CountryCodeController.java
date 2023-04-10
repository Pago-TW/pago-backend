package tw.pago.pagobackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;

import tw.pago.pagobackend.model.Country;

@RestController
public class CountryCodeController {
    @GetMapping("/countries")
    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();
        for (CountryCode code : CountryCode.values()) {
            String name = code.getName();
            countries.add(new Country(code, name));
        }
        return countries;
    }
    
}
