package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateTripCollectionRequestDto {
  private String tripCollectionId;
  private String tripCollectionName;
}
