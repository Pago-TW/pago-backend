package tw.pago.pagobackend.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.TripStatusEnum;

@Data
@NoArgsConstructor
public class TripCollectionResponseDto {
  private String tripCollectionId;
  private String tripCollectionName;
  private TripStatusEnum tripCollectionStatus;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;
  private List<TripResponseDto> trips;

}
