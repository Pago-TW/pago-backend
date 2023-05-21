package tw.pago.pagobackend.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MatchingShopperListDto {

  private List<MatchingShopperResponseDto> matchingShopperResponseDtoList;
  private Integer totalMatchingShoppers;
}
