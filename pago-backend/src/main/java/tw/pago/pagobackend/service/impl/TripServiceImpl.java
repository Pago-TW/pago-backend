package tw.pago.pagobackend.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.assembler.TripAssembler;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripDashboardDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.CountryUtil;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
@AllArgsConstructor
public class TripServiceImpl implements TripService {

  private final TripAssembler tripAssembler;
  private final TripDao tripDao;
  private final UuidGenerator uuidGenerator;
  private final BidDao bidDao;
  private final OrderDao orderDao;
  private final OrderService orderService;
  private final UserDao userDao;

  @Override
  public Trip getTripById(String tripId) {
    Trip trip = tripDao.getTripById(tripId);

    List<Bid> bidListForTrip = getBidListForTrip(tripId);
    List<Order> orderListForChosenBids = getOrderListForChosenBids(bidListForTrip);
    BigDecimal totalProfit = calculateProfit(orderListForChosenBids);

    trip.setProfit(totalProfit);

    return trip;
  }

  @Override
  public TripResponseDto getTripResponseDtoByTrip(Trip trip) {
    // Convert the Trip model to TripResponseDto
    TripResponseDto tripResponseDto = tripAssembler.assemble(trip);

    // Set default currency to TWD
    tripResponseDto.setCurrency(CurrencyEnum.TWD);

    // Get/Set countries & cities chineseName
    String fromCountryChineseName = CountryUtil.getChineseCountryName(tripResponseDto.getFromCountry());
    String fromCityChineseName = tripResponseDto.getFromCity().getChineseName();
    String toCountryChineseName = CountryUtil.getChineseCountryName(tripResponseDto.getToCountry());
    String toCityChineseName = tripResponseDto.getToCity().getChineseName();
    tripResponseDto.setFromCountryChineseName(fromCountryChineseName);
    tripResponseDto.setFromCityChineseName(fromCityChineseName);
    tripResponseDto.setToCountryChineseName(toCountryChineseName);
    tripResponseDto.setToCityChineseName(toCityChineseName);

    // Set the trip status based on the trip arrivalDate
    setTripStatusBasedOnDates(tripResponseDto);

    // Get the list of bids for the given trip
    List<Bid> bidListForTrip = getBidListForTrip(trip.getTripId());
    // Get the list of orders for the chosen bids
    List<Order> orderListForChosenBids = getOrderListForChosenBids(bidListForTrip);

    // Calculate the total profit for the trip using the orders list
    BigDecimal totalProfit = calculateProfit(orderListForChosenBids);
    tripResponseDto.setProfit(totalProfit);

    // Get the dashboard counts (totalRequested, totalToBePurchased, totalToBeDelivered)
    TripDashboardDto tripDashboardDto = getTripDashboardCounts(bidListForTrip,
        orderListForChosenBids);
    tripResponseDto.setDashboard(tripDashboardDto);

    return tripResponseDto;
  }

  @Override
  public User getUserByShopperId(String shopperId) {

    User shopper = userDao.getUserById(shopperId);

    return shopper;
  }


  @Override
  public String createTrip(String userId, CreateTripRequestDto createTripRequestDto) {
    String tripUuid = uuidGenerator.getUuid();
    createTripRequestDto.setTripId(tripUuid);
    return tripDao.createTrip(userId, createTripRequestDto);
  }

  @Override
  public void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto) {
    tripDao.updateTrip(trip, updateTripRequestDto);
  }

  @Override
  public void deleteTripById(String tripId) throws SQLException {
    tripDao.delete(tripId);
  }

  @Override
  public List<Trip> getTripList(ListQueryParametersDto listQueryParametersDto) {

    List<Trip> tripList = tripDao.getTripList(listQueryParametersDto);
    return tripList;
  }

  @Override
  public List<Trip> getMatchingTripListByOrderId(String orderId, ListQueryParametersDto listQueryParametersDto) {

    Order order = orderService.getOrderById(orderId);
    Date latestReceiveItemDate = order.getLatestReceiveItemDate();
    LocalDate latestReceiveItemLocalDate = latestReceiveItemDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date orderCreateDate = order.getCreateDate();
    LocalDate orderCreateLocalDate = orderCreateDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


    listQueryParametersDto.setOrderId(orderId);
    listQueryParametersDto.setToCity(order.getDestinationCity());
    listQueryParametersDto.setFromCity(order.getOrderItem().getPurchaseCity());
    listQueryParametersDto.setLatestReceiveItemDate(latestReceiveItemLocalDate);
    listQueryParametersDto.setOrderCreateDate(orderCreateLocalDate);


    List<Trip> tripList = tripDao.getMatchingTripListForOrder(listQueryParametersDto);

    return tripList;
  }


  @Override
  public List<OrderResponseDto> getMatchingOrderResponseDtoListForTrip(
      ListQueryParametersDto listQueryParametersDto, Trip trip) {

    List<Order> matchingOrderListForTrip = orderService.getMatchingOrderListForTrip(
        listQueryParametersDto, trip);

    List<OrderResponseDto> orderResponseDtoList = orderService.getOrderResponseDtoListByOrderList(
        matchingOrderListForTrip);

    return orderResponseDtoList;
  }

  @Override
  public List<TripResponseDto> getTripResponseDtoList(
      ListQueryParametersDto listQueryParametersDto) {

    List<Trip> tripList = getTripList(listQueryParametersDto);

    List<TripResponseDto> tripResponseDtoList = tripList.stream()
        .map(this::getTripResponseDtoByTrip)
        .collect(Collectors.toList());

    return tripResponseDtoList;
  }

  @Override
  public List<TripResponseDto> getTripResponseDtoByTripList(List<Trip> tripList) {

    List<TripResponseDto> tripResponseDtoList = tripList.stream()
        .map(this::getTripResponseDtoByTrip)
        .collect(Collectors.toList());

    return tripResponseDtoList;
  }

  @Override
  public Integer countTrip(ListQueryParametersDto listQueryParametersDto) {
    Integer total = tripDao.countTrip(listQueryParametersDto);

    return total;
  }

  @Override
  public Integer countMatchingShopper(ListQueryParametersDto listQueryParametersDto) {
    Integer total = tripDao.countMatchingShopper(listQueryParametersDto);

    return total;
  }

  @Override
  public Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto,
      Trip trip) {

    Integer total = orderService.countMatchingOrderForTrip(listQueryParametersDto, trip);

    return total;
  }


  private void setTripStatusBasedOnDates(TripResponseDto tripResponseDto) {
    LocalDate currentDate = LocalDate.now();

    // Change arrivalDate data type from `Date` to `LocalDate`
    Instant arrivalDateInstant = tripResponseDto.getArrivalDate().toInstant();
    LocalDate arrivalDate = arrivalDateInstant.atZone(ZoneOffset.UTC).toLocalDate();

    // Set TripStatus by comparing with currentDate
    int maxDaysOfTrip = 7;
    LocalDate endDateOfTrip = arrivalDate.plusDays(maxDaysOfTrip);

    if (currentDate.isBefore(arrivalDate)) {
      tripResponseDto.setTripStatus(TripStatusEnum.UPCOMING);
    } else if (currentDate.isAfter(arrivalDate) && currentDate.isBefore(endDateOfTrip)) {
      tripResponseDto.setTripStatus(TripStatusEnum.ONGOING);
    } else {
      tripResponseDto.setTripStatus(TripStatusEnum.PAST);
    }
  }


  private List<Bid> getBidListForTrip(String tripId) {
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .orderBy("create_date")
        .sort("DESC")
        .startIndex(0)
        .size(999)
        .tripId(tripId)
        .build();

    List<Bid> bidListForTrip = bidDao.getBidList(listQueryParametersDto);

    return bidListForTrip;
  }

  private List<Order> getOrderListForChosenBids(List<Bid> bidListForTrip) {
    List<Bid> isChosenBidList = new ArrayList<>();
    List<Order> orderListForChosenBids = new ArrayList<>();

    for (Bid bid : bidListForTrip) {
      if (bid.getBidStatus().equals(BidStatusEnum.IS_CHOSEN)) {
        isChosenBidList.add(bid);
      }
    }

    for (Bid bid : isChosenBidList) {
      Order order = orderDao.getOrderById(bid.getOrderId());
      orderListForChosenBids.add(order);
    }

    return orderListForChosenBids;
  }

  private TripDashboardDto getTripDashboardCounts(List<Bid> bidListForTrip,
      List<Order> orderListForChosenBids) {
    int totalRequested =
        bidListForTrip.size() - orderListForChosenBids.size(); // ALL_BIDS - IS_CHOSEN = NOT_CHOSEN
    int totalToBePurchased = 0;
    int totalToBeDelivered = 0;

    for (Order order : orderListForChosenBids) {
      if (order.getOrderStatus().equals(OrderStatusEnum.TO_BE_PURCHASED)) {
        totalToBePurchased += 1;
      } else if (order.getOrderStatus().equals(OrderStatusEnum.TO_BE_DELIVERED)) {
        totalToBeDelivered += 1;
      }
    }

    TripDashboardDto tripDashboardDto = new TripDashboardDto();
    tripDashboardDto.setRequested(totalRequested);
    tripDashboardDto.setToBePurchased(totalToBePurchased);
    tripDashboardDto.setToBeDelivered(totalToBeDelivered);

    return tripDashboardDto;
  }


  private BigDecimal calculateProfit(List<Order> orderListForChosenBids) {
    BigDecimal totalProfit = BigDecimal.valueOf(0);

    for (Order order : orderListForChosenBids) {
      totalProfit = totalProfit.add(order.getTravelerFee());
    }

    return totalProfit;
  }

}
