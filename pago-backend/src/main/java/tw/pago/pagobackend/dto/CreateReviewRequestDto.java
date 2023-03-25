package tw.pago.pagobackend.dto;

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
  private Integer rating;
  private ReviewTypeEnum reviewType;

}
