package tw.pago.pagobackend.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.ReviewTypeEnum;


@Getter
@Setter
@Builder
public class CreateReviewRequestDto {
  private String reviewId;
  private String orderId;
  private String creatorId;
  private String targetId;
  private String content;

  @Min(value = 0, message = "Rating must be at least 0")
  @Max(value = 5, message = "Rating can be at most 5")
  private Integer rating;
  private ReviewTypeEnum reviewType;

}
