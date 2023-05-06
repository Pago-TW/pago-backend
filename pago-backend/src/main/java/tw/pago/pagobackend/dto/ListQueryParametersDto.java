package tw.pago.pagobackend.dto;


import com.neovisionaries.i18n.CountryCode;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.constant.TripStatusEnum;

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
  private CityCode fromCity;
  private CityCode toCity;
  private Boolean isPackagingRequired;
  private LocalDate orderCreateDate;
  private CountryCode purchaseCountry;
  private CityCode purchaseCity;
  private CountryCode destinationCountry;
  private CityCode destinationCity;

  // Order, Trip
  private String userId;
  private String consumerId;

  // Review
  private String targetId;
  private ReviewTypeEnum reviewType;


  // Bid
  private String orderId;
  private BidStatusEnum bidStatus;

  // Trip
  private String tripId;
  private LocalDate latestReceiveItemDate;
  private TripStatusEnum tripStatus;

  // Chatroom
  private String chatroomId;
}
