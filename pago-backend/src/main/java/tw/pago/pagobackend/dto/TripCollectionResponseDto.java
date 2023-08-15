package tw.pago.pagobackend.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripCollectionResponseDto {
  private String tripCollectionId;
  private String tripCollectionName;
  private List<TripResponseDto> trips;

}
