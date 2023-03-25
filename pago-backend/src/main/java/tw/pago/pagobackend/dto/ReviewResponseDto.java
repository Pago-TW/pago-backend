package tw.pago.pagobackend.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.model.User;

@Data
@NoArgsConstructor
public class ReviewResponseDto {
  private String reviewId;
  private ReviewCreatorDto creator;
  private ReviewTargetDto target;
  private ReviewOrderDto order;
  private String content;
  private Integer rating;
  private ReviewTypeEnum reviewType;
  private Date createDate;
  private Date updateDate;

}
