package tw.pago.pagobackend.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ReviewCreatorDto {
  private String userId;
  private String fullName;
  private String avatarUrl;

}
