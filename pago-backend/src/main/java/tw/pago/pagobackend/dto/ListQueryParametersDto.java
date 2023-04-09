package tw.pago.pagobackend.dto;


import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import tw.pago.pagobackend.constant.CityCode;
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
  private CityCode from;
  private CityCode to;
  private Boolean isPackagingRequired;
  private LocalDate orderCreateDate;

  // Order, Trip
  private String userId;

  // Trip
  private LocalDate latestReceiveItemDate;

  // Review
  private String targetId;
  private ReviewTypeEnum reviewType;


  // Bid
  private String orderId;

  // Trip
  private String tripId;

  //
}
