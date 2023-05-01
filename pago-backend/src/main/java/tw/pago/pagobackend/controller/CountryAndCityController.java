package tw.pago.pagobackend.controller;


import com.neovisionaries.i18n.CountryCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.dto.CountryAndCityResponseDto;
import tw.pago.pagobackend.model.City;
import tw.pago.pagobackend.model.Country;
import tw.pago.pagobackend.util.CountryUtil;

@RestController
@AllArgsConstructor
public class CountryAndCityController {

  private final CountryUtil countryUtil;

//  @Deprecated
//  @GetMapping("/countries")
//  public ResponseEntity<CountryAndCityResponseDto> getCountries() {
//    List<Country> countries = new ArrayList<>();
//    for (CountryCode code : CountryCode.values()) {
//      String englishName = CountryUtil.getEnglishCountryName(code);
//      String chineseName = CountryUtil.getChineseCountryName(code);
//      countries.add(new Country(code, englishName, chineseName));
//    }
//
//    CountryAndCityResponseDto countryAndCityResponseDto = new CountryAndCityResponseDto();
//    countryAndCityResponseDto.setCountries(countries);
//
//    return ResponseEntity.status(HttpStatus.OK).body(countryAndCityResponseDto);
//  }
//
//  @Deprecated
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

//  @Deprecated
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

  @GetMapping("/countries-and-cities")
  public List<CountryAndCityResponseDto> getLocations(
      @RequestParam(required = false) String country,
      @RequestParam(required = false) String city) {

    List<CountryAndCityResponseDto> locations = new ArrayList<>();

    for (CityCode cityCode : CityCode.values()) {
      if ((country != null && !cityCode.getCountryCode().getAlpha2().equalsIgnoreCase(country)) ||
          (city != null && !cityCode.name().equalsIgnoreCase(city))) {
        continue;
      }

      Country countryObject = new Country();
      City cityObject = new City();

      CountryCode countryCode = cityCode.getCountryCode();
      String countryEnglishName = CountryUtil.getEnglishCountryName(countryCode);
      String countryChineseName = CountryUtil.getChineseCountryName(countryCode);

      String cityEnglishName = cityCode.getEnglishName();
      String cityChineseName = cityCode.getChineseName();

      countryObject.setCountryCode(countryCode);
      countryObject.setEnglishName(countryEnglishName);
      countryObject.setChineseName(countryChineseName);
      cityObject.setCityCode(cityCode.name());
      cityObject.setEnglishName(cityEnglishName);
      cityObject.setChineseName(cityChineseName);

      CountryAndCityResponseDto countryAndCityResponseDto = new CountryAndCityResponseDto(countryObject, cityObject);
      locations.add(countryAndCityResponseDto);
    }

    return locations;
  }




}
