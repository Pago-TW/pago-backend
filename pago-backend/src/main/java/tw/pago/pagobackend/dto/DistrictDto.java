package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DistrictDto {
  private String zipCode;
  private String districtChineseName;
  private String districtEnglishName;
}
