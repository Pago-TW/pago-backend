package tw.pago.pagobackend.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.ReviewTypeEnum;

@Data
@NoArgsConstructor
public class BidCreatorReviewDto {
  private double averageRating;
  private Integer totalReview;
  private ReviewTypeEnum reviewType;

}
