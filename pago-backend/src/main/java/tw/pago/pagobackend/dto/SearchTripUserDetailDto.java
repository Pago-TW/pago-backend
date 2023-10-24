package tw.pago.pagobackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.ReviewTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchTripUserDetailDto {
  private String avatarUrl;
  private String fullName;
  private double averageRating;
  private Integer totalReview;
  private ReviewTypeEnum reviewType;

}
