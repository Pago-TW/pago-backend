package tw.pago.pagobackend.model;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripCollection {
  private String tripCollectionId;
  private String creatorId;
  private String tripCollectionName;
  private ZonedDateTime createDate;
  private ZonedDateTime updateDate;

}
