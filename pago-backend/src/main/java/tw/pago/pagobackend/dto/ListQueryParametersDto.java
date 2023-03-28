package tw.pago.pagobackend.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.ReviewTypeEnum;

@Getter
@Setter
@Builder
public class ListQueryParametersDto {
  private Integer startIndex;
  private Integer size;
  private String orderBy;
  private String sort;
  private String search;


  // Order
  private OrderStatusEnum orderStatus;
  private String userId;


  // Review
  private String targetId;
  private ReviewTypeEnum reviewType;


  // Bid
  private String orderId;
}
