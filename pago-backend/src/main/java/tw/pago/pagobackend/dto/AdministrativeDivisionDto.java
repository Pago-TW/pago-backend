package tw.pago.pagobackend.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AdministrativeDivisionDto {
  private String administrativeDivisionChineseName;
  private String administrativeDivisionEnglishName;
  private List<DistrictDto> districtList;

}
