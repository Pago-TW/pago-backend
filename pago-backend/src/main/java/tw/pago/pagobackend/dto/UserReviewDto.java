package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.ReviewTypeEnum;

@Data
@NoArgsConstructor
public class UserReviewDto {
  private double averageRating;
  private Integer totalReview;
  private ReviewTypeEnum reviewType;

}
