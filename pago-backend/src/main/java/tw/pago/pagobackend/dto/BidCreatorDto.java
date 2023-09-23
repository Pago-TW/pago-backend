package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BidCreatorDto {
  private String userId;
  private String fullName;
  private String avatarUrl;
  @JsonProperty("isTraveling")
  private boolean isTraveling;
  private BidCreatorReviewDto review;

}
