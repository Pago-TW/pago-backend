package tw.pago.pagobackend.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripCollectionResponseDto {
  private String tripCollectionId;
  private String tripCollectionName;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
  private List<TripResponseDto> trips;

}
