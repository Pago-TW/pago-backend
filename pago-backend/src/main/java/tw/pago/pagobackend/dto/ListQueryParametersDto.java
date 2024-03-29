package tw.pago.pagobackend.dto;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.constant.NotificationTypeEnum;
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
  private CountryCode fromCountry;
  private CityCode fromCity;
  private CountryCode toCountry;
  private CityCode toCity;
  private Boolean isPackagingRequired;
  private LocalDate orderCreateDate;
  private CountryCode purchaseCountry;
  private CityCode purchaseCity;
  private CountryCode destinationCountry;
  private CityCode destinationCity;
  private BigDecimal minTravelerFee;
  private BigDecimal maxTravelerFee;
  private Date orderLatestReceiveItemDate; // TODO 待完成 LocalDateTime -> ZonedDateTime，要修正這裡的命名為latestReceiveItemDate

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

  // Notification
  private NotificationTypeEnum notificationType;

  // TransactionRecord
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
}
