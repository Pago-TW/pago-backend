//package tw.pago.pagobackend.controller;
//
//import com.neovisionaries.i18n.CountryCode;
//import java.util.ArrayList;
//import java.util.List;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import tw.pago.pagobackend.constant.CityCode;
//import tw.pago.pagobackend.dto.CountryAndCityResponseDto;
//import tw.pago.pagobackend.model.City;
//import tw.pago.pagobackend.model.Country;
//import tw.pago.pagobackend.util.CountryUtil;
//
//@RestController
//public class CityCodeController {
//
//  @GetMapping("/cities")
//  public ResponseEntity<CountryAndCityResponseDto> getCities() {
//    List<City> cities = new ArrayList<>();
//    for (CityCode code: CityCode.values()) {
//      String cityCode = code.name();
//      String englishName = code.getEnglishName();
//      String chineseName = code.getChineseName();
//      CountryCode countryCode = code.getCountryCode();
//
//      cities.add(new City(cityCode, englishName, chineseName, countryCode));
//    }
//    CountryAndCityResponseDto countryAndCityResponseDto = new CountryAndCityResponseDto();
//    countryAndCityResponseDto.setCities(cities);
//
//    return ResponseEntity.status(HttpStatus.OK).body(countryAndCityResponseDto);
//  }
//
//  @GetMapping("/countries-and-cities")
//  public ResponseEntity<CountryAndCityResponseDto> getCountryAndCity() {
//    List<Country> countries = new ArrayList<>();
//    for (CountryCode code : CountryCode.values()) {
//      String englishName = CountryUtil.getEnglishCountryName(code);
//      String chineseName = CountryUtil.getChineseCountryName(code);
//
//      countries.add(new Country(code, englishName, chineseName));
//    }
//
//    List<City> cities = new ArrayList<>();
//    for (CityCode code: CityCode.values()) {
//      String cityCode = code.name();
//      String englishName = code.getEnglishName();
//      String chineseName = code.getChineseName();
//      CountryCode countryCode = code.getCountryCode();
//
//      cities.add(new City(cityCode, englishName, chineseName, countryCode));
//    }
//
//    CountryAndCityResponseDto countryAndCityResponseDto = new CountryAndCityResponseDto();
//    countryAndCityResponseDto.setCountries(countries);
//    countryAndCityResponseDto.setCities(cities);
//
//    return ResponseEntity.status(HttpStatus.OK).body(countryAndCityResponseDto);
//  }
//
//}
