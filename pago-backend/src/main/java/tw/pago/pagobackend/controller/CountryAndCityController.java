package tw.pago.pagobackend.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.dto.AdministrativeDivisionDto;
import tw.pago.pagobackend.dto.CountryAndCityResponseDto;
import tw.pago.pagobackend.model.City;
import tw.pago.pagobackend.model.Country;
import tw.pago.pagobackend.util.CountryUtil;

@RestController
@AllArgsConstructor
public class CountryAndCityController {

  private final CountryUtil countryUtil;
  private final ResourceLoader resourceLoader;
  private List<AdministrativeDivisionDto> administrativeDivisionDtoList;

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
      @RequestParam(defaultValue = "false") boolean includeAny,
      @RequestParam(required = false) String country,
      @RequestParam(required = false) String city) {

    List<CountryAndCityResponseDto> locations = new ArrayList<>();

    // Add the "Any Country", "Any City" combination at the beginning
    if (includeAny) {

      Country anyCountry = new Country();
      anyCountry.setCountryCode("ANY");
      anyCountry.setChineseName("任何國家");
      anyCountry.setEnglishName("Any Country");
      City anyCity = new City();
      anyCity.setCityCode("ANY");
      anyCity.setChineseName("任何城市");
      anyCity.setEnglishName("Any City");
      locations.add(new CountryAndCityResponseDto(anyCountry, anyCity));
    }

    Set<CountryCode> addedCountries = new HashSet<>();

    for (CityCode cityCode : CityCode.values()) {
      if ((country != null && !cityCode.getCountryCode().getAlpha2().equalsIgnoreCase(country)) ||
          (city != null && !cityCode.name().equalsIgnoreCase(city))) {
        continue;
      }

      CountryCode countryCode = cityCode.getCountryCode();
      if (includeAny && cityCode.getCountryCode() != null && addedCountries.add(cityCode.getCountryCode())) {
        // Add the "Country", "Any City" combination for each country
        Country countryObject = new Country();
        countryObject.setCountryCode(countryCode.name());
        countryObject.setChineseName(CountryUtil.getChineseCountryName(countryCode));
        countryObject.setEnglishName(CountryUtil.getEnglishCountryName(countryCode));
        City anyCity = new City();
        anyCity.setCityCode("ANY");
        anyCity.setChineseName("任何城市");
        anyCity.setEnglishName("Any City");
        locations.add(new CountryAndCityResponseDto(countryObject, anyCity));
      }

      if (countryCode != null) {
        Country countryObject = new Country();
        countryObject.setCountryCode(countryCode.name());
        countryObject.setChineseName(CountryUtil.getChineseCountryName(countryCode));
        countryObject.setEnglishName(CountryUtil.getEnglishCountryName(countryCode));
        City cityObject = new City();
        cityObject.setCityCode(cityCode.name());
        cityObject.setChineseName(cityCode.getChineseName());
        cityObject.setEnglishName(cityCode.getEnglishName());

        CountryAndCityResponseDto countryAndCityResponseDto = new CountryAndCityResponseDto(countryObject, cityObject);
        locations.add(countryAndCityResponseDto);
      }
    }

    return locations;
  }


  @GetMapping("/administrative-divisions")
  public ResponseEntity<List<String>> getAdministrativeDivision() {

    List<String> taiwanAdministrativeDivision = Arrays.asList(
        "台北市",
        "新北市",
        "基隆市",
        "桃園市",
        "新竹市",
        "新竹縣",
        "苗栗縣",
        "台中市",
        "彰化縣",
        "南投縣",
        "雲林縣",
        "嘉義市",
        "嘉義縣",
        "台南市",
        "高雄市",
        "屏東縣",
        "宜蘭縣",
        "花蓮縣",
        "台東縣",
        "澎湖縣",
        "金門縣",
        "連江縣"
    );

    return ResponseEntity.status(HttpStatus.OK).body(taiwanAdministrativeDivision);
  }


  @PostConstruct
  public void init() throws IOException {
    Resource resource = resourceLoader.getResource("classpath:TaiwanCityCountyData.json");
    InputStream input = resource.getInputStream();
    ObjectMapper mapper = new ObjectMapper();
    administrativeDivisionDtoList = mapper.readValue(input, new TypeReference<List<AdministrativeDivisionDto>>() {});
  }


  @GetMapping("/districts")
  public ResponseEntity<?> getDistrictsList(@RequestParam(required = false) String administrativeDivision) {
    if (administrativeDivision != null) {
      Optional<AdministrativeDivisionDto> matchingDivision = administrativeDivisionDtoList.stream()
          .filter(administrativeDivisionDto -> administrativeDivisionDto.getAdministrativeDivisionChineseName().equals(administrativeDivision))
          .findFirst();

      return matchingDivision
          .map(division -> ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(division)))
          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    return ResponseEntity.status(HttpStatus.OK).body(administrativeDivisionDtoList);
  }

}
