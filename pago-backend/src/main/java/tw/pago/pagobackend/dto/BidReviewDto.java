package tw.pago.pagobackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.ReviewTypeEnum;


@Getter
@Setter
@Builder
public class BidReviewDto {
  private double averageRating;
  private Integer totalReview;
  private ReviewTypeEnum reviewType;

}
