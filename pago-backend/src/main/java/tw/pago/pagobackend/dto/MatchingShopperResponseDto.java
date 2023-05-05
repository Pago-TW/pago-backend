package tw.pago.pagobackend.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.model.Trip;

@Data
@NoArgsConstructor
@JsonPropertyOrder({
    "userId",
    "fullName",
    "email",
    "avatarUrl",
})
public class MatchingShopperResponseDto {

  private String userId;
  @JsonIgnore
  private String firstName;
  @JsonIgnore
  private String lastName;
  private String email;
  private String avatarUrl;

  // For programing use;
  private String fullName;

  private List<MatchingTripForOrderDto> trips;

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
