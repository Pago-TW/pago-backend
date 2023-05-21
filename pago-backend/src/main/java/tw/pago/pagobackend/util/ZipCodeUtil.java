package tw.pago.pagobackend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dto.AdministrativeDivisionDto;
import tw.pago.pagobackend.dto.DistrictDto;

@Component
@AllArgsConstructor
public class ZipCodeUtil {
  private final ResourceLoader resourceLoader;
  private List<AdministrativeDivisionDto> administrativeDivisionDtoList;

  @PostConstruct
  public void init() throws IOException {
    Resource resource = resourceLoader.getResource("classpath:TaiwanCityCountyData.json");
    InputStream input = resource.getInputStream();
    ObjectMapper mapper = new ObjectMapper();
    administrativeDivisionDtoList = mapper.readValue(input, new TypeReference<List<AdministrativeDivisionDto>>() {});
  }

  public String getCityAndDistrictByZipCode(String zipCode) {
    for (AdministrativeDivisionDto administrativeDivisionDto: administrativeDivisionDtoList) {
      Optional<DistrictDto> optionalDistrictDto = administrativeDivisionDto.getDistrictList()
          .stream()
          .filter(districtDto -> districtDto.getZipCode().equals(zipCode))
          .findFirst();
      if (optionalDistrictDto.isPresent()){
        return administrativeDivisionDto.getAdministrativeDivisionChineseName() + optionalDistrictDto.get().getDistrictChineseName();
      }
    }
    return "Zip Code Not Found!";
  }



}
