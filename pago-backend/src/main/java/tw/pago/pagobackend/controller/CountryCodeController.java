//package tw.pago.pagobackend.controller;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.neovisionaries.i18n.CountryCode;
//
//import tw.pago.pagobackend.dto.CountryAndCityResponseDto;
//import tw.pago.pagobackend.model.Country;
//import tw.pago.pagobackend.util.CountryUtil;
//
//@RestController
//@AllArgsConstructor
//public class CountryCodeController {
//
//    private final CountryUtil countryUtil;
//
//    @GetMapping("/countries")
//    public ResponseEntity<CountryAndCityResponseDto> getCountries() {
//        List<Country> countries = new ArrayList<>();
//        for (CountryCode code : CountryCode.values()) {
//            String englishName = CountryUtil.getEnglishCountryName(code);
//            String chineseName = CountryUtil.getChineseCountryName(code);
//            countries.add(new Country(code, englishName, chineseName));
//        }
//
//        CountryAndCityResponseDto countryAndCityResponseDto = new CountryAndCityResponseDto();
//        countryAndCityResponseDto.setCountries(countries);
//
//        return ResponseEntity.status(HttpStatus.OK).body(countryAndCityResponseDto);
//    }
//
//}
