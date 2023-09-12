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
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.assembler.TripAssembler;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dao.TripCollectionDao;
import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.BatchCreateTripRequestDto;
import tw.pago.pagobackend.dto.BatchCreateTripRequestDto.Departure;
import tw.pago.pagobackend.dto.BatchCreateTripRequestDto.Stop;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripCollectionResponseDto;
import tw.pago.pagobackend.dto.TripDashboardDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.exception.AccessDeniedException;
import tw.pago.pagobackend.exception.ConflictException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.TripCollection;
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
  private final TripCollectionDao tripCollectionDao;
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
  public TripCollection getTripCollectionById(String tripCollectionId) {
    return tripCollectionDao.getTripCollectionById(tripCollectionId);
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
    if (isDuplicateTrip(userId, createTripRequestDto)) {
      throw new ConflictException("A trip with the same details already exist!");
    }
    String tripUuid = uuidGenerator.getUuid();
    createTripRequestDto.setTripId(tripUuid);
    return tripDao.createTrip(userId, createTripRequestDto);
  }

  @Override
  public void createTrip(CreateTripRequestDto createTripRequestDto) {
    if (isDuplicateTrip(createTripRequestDto.getShopperId(), createTripRequestDto)) {
      throw new ConflictException("A trip with the same details already exist!");
    }
    String tripId = uuidGenerator.getUuid();
    createTripRequestDto.setTripId(tripId);
    tripDao.createTrip(createTripRequestDto);
  }

  @Override
  @Transactional
  public TripCollection batchCreateTrip(BatchCreateTripRequestDto batchCreateTripRequestDto) {
    String tripCollectionId = uuidGenerator.getUuid();
    String firstTripId = uuidGenerator.getUuid();


    // Create Trip Collection for new trips
    CreateTripCollectionRequestDto createTripCollectionRequestDto = new CreateTripCollectionRequestDto();
    createTripCollectionRequestDto.setTripCollectionId(tripCollectionId);
    createTripCollectionRequestDto.setTripCollectionName(batchCreateTripRequestDto.getTripCollectionName());
    createTripCollectionRequestDto.setCreatorId(batchCreateTripRequestDto.getShopperId());
    createTripCollection(createTripCollectionRequestDto);


    // Create First trip, departure City -> stops.get(0)
    Departure departure = batchCreateTripRequestDto.getDeparture();
    Stop firstStop = batchCreateTripRequestDto.getStops().get(0); // The arrival city of first trip
    CreateTripRequestDto createTripRequestDto = new CreateTripRequestDto();
    createTripRequestDto.setShopperId(batchCreateTripRequestDto.getShopperId());
    createTripRequestDto.setTripId(firstTripId);
    createTripRequestDto.setTripCollectionId(tripCollectionId);
    createTripRequestDto.setFromCountry(departure.getCountry());
    createTripRequestDto.setFromCity(departure.getCity());
    createTripRequestDto.setToCountry(firstStop.getCountry());
    createTripRequestDto.setToCity(firstStop.getCity());
    createTripRequestDto.setArrivalDate(firstStop.getArrivalDate());
    createTrip(createTripRequestDto);

    for (int i = 0; i < batchCreateTripRequestDto.getStops().size() - 1; i++) {
      Stop departureStop = batchCreateTripRequestDto.getStops().get(i);
      Stop nextStop = batchCreateTripRequestDto.getStops().get(i+1);
      String tripId = uuidGenerator.getUuid();

      createTripRequestDto.setTripId(tripId);
      createTripRequestDto.setFromCountry(departureStop.getCountry());
      createTripRequestDto.setFromCity(departure.getCity());
      createTripRequestDto.setToCountry(nextStop.getCountry());
      createTripRequestDto.setToCity(nextStop.getCity());
      createTripRequestDto.setArrivalDate(nextStop.getArrivalDate());
      createTrip(createTripRequestDto);
    }

    return getTripCollectionById(tripCollectionId);
  }

  @Override
  public void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto) {
    tripDao.updateTrip(trip, updateTripRequestDto);
  }

  @Override
  @Transactional
  public void deleteTripById(String tripId) throws SQLException {
    List<Bid> bidList = bidDao.getBidListByTripId(tripId);

    boolean hasChosenBid = bidList.stream()
        .anyMatch(bid -> bid.getBidStatus().equals(BidStatusEnum.IS_CHOSEN));

    if (hasChosenBid) {
      throw new AccessDeniedException("TripId: " + tripId + ",\nYou have matched at least one order, so you have no permission to delete this trip ");
    }

    tripDao.delete(tripId);
  }

  @Override
  @Transactional
  public void deleteTripByTrip(Trip trip) throws SQLException {
    String tripId = trip.getTripId();

    List<Bid> bidList = bidDao.getBidListByTripId(tripId);

    boolean hasChosenBid = bidList.stream()
        .anyMatch(bid -> bid.getBidStatus().equals(BidStatusEnum.IS_CHOSEN));

    if (hasChosenBid) {
      throw new AccessDeniedException("TripId: " + tripId + ",\nYou have matched at least one order, so you have no permission to delete this trip ");
    }

    tripDao.delete(tripId);

  }

  @Override
  public void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto) {
    tripCollectionDao.createTripCollection(createTripCollectionRequestDto);
  }


  @Override
  public List<Trip> getTripList(ListQueryParametersDto listQueryParametersDto) {

    List<Trip> tripList = tripDao.getTripList(listQueryParametersDto);
    return tripList;
  }

  @Override
  public List<Trip> getTripsByShopperId(String shopperId) {
    return tripDao.getTripsByShopperId(shopperId);
  }

  @Override
  public List<Trip> getTripListByTripColletionId(String tripCollectionId) {
    return tripDao.getTripListByTripColletionId(tripCollectionId);
  }

  @Override
  public List<Trip> getMatchingTripListByOrderId(String orderId, ListQueryParametersDto listQueryParametersDto) {

    Order order = orderService.getOrderById(orderId);
    CountryCode purchaseCountry = order.getOrderItem().getPurchaseCountry();
    CityCode purchaseCity = order.getOrderItem().getPurchaseCity();
    CountryCode destinationCountry = order.getDestinationCountry();
    CityCode destinationCity = order.getDestinationCity();
    Date latestReceiveItemDate = order.getLatestReceiveItemDate();
    LocalDate latestReceiveItemLocalDate = latestReceiveItemDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date orderCreateDate = order.getCreateDate();
    LocalDate orderCreateLocalDate = orderCreateDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


    listQueryParametersDto.setOrderId(orderId);
    listQueryParametersDto.setPurchaseCountry(purchaseCountry);
    listQueryParametersDto.setPurchaseCity(purchaseCity);
    listQueryParametersDto.setDestinationCountry(destinationCountry);
    listQueryParametersDto.setDestinationCity(destinationCity);
    listQueryParametersDto.setLatestReceiveItemDate(latestReceiveItemLocalDate);
    listQueryParametersDto.setOrderCreateDate(orderCreateLocalDate);


    List<Trip> tripList = tripDao.getMatchingTripListForOrder(listQueryParametersDto);

    return tripList;
  }

  @Override
  public List<Trip> getTripListByTripStatus(TripStatusEnum tripStatus,
      ListQueryParametersDto listQueryParametersDto) {

    List<Trip> tripList = tripDao.getTripListByTripStatus(tripStatus, listQueryParametersDto);
    return tripList;
  }

  @Override
  public List<TripCollection> getTripCollectionList(ListQueryParametersDto listQueryParametersDto) {
    return tripCollectionDao.getTripCollectionList(listQueryParametersDto);
  }

  @Override
  public List<TripCollection> getTripCollectionListByCreatorId(String creatorId) {

    return tripCollectionDao.getTripCollectionListByCreatorId(creatorId);
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

    Stream<TripResponseDto> tripResponseDtoStream = tripList.stream()
        .map(this::getTripResponseDtoByTrip);

    if (listQueryParametersDto.getTripStatus() != null) {
      tripResponseDtoStream  = tripResponseDtoStream
          .filter(tripResponseDto -> tripResponseDto.getTripStatus().equals(listQueryParametersDto.getTripStatus()));
    }

    List<TripResponseDto> tripResponseDtoList = tripResponseDtoStream.collect(Collectors.toList());

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
  public List<TripCollectionResponseDto> getTripCollectionResponseDtoListByTripCollectionList(
      List<TripCollection> tripCollectionList) {

    return tripCollectionList.stream()
        .map(this::getTripCollectionResponseDtoByTripCollection)
        .collect(Collectors.toList());
  }

  @Override
  public TripCollectionResponseDto getTripCollectionResponseDtoByTripCollection(
      TripCollection tripCollection) {

    List<Trip> tripList = getTripListByTripColletionId(tripCollection.getTripCollectionId());
    List<TripResponseDto> tripResponseDtoList = getTripResponseDtoByTripList(tripList);

    TripStatusEnum tripCollectionStatus = getTripCollectionStatusBasedOnEachTripStatus(tripResponseDtoList);

    TripCollectionResponseDto tripCollectionResponseDto = new TripCollectionResponseDto();
    tripCollectionResponseDto.setTripCollectionId(tripCollection.getTripCollectionId());
    tripCollectionResponseDto.setTripCollectionName(tripCollection.getTripCollectionName());
    tripCollectionResponseDto.setTripCollectionStatus(tripCollectionStatus);
    tripCollectionResponseDto.setCreateDate(tripCollection.getCreateDate());
    tripCollectionResponseDto.setUpdateDate(tripCollection.getUpdateDate());
    tripCollectionResponseDto.setTrips(tripResponseDtoList);

    return tripCollectionResponseDto;
  }


  @Override
  public Integer countTrip(ListQueryParametersDto listQueryParametersDto) {
    Integer total = tripDao.countTrip(listQueryParametersDto);

    return total;
  }

  @Override
  public Integer countTrip(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto) {

    Integer total = tripDao.countTrip(tripStatus, listQueryParametersDto);
    return total;
  }

  @Override
  public Integer countTripCollection(ListQueryParametersDto listQueryParametersDto) {
    return tripCollectionDao.countTripCollection(listQueryParametersDto);
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

  public boolean isDuplicateTrip(String shopperId, CreateTripRequestDto createTripRequestDto) {
    List<Trip> existingTrips = tripDao.getTripsByShopperId(shopperId);
    for (Trip trip : existingTrips) {
        if (trip.getFromCountry().equals(createTripRequestDto.getFromCountry()) &&
            trip.getToCountry().equals(createTripRequestDto.getToCountry()) &&
            trip.getFromCity().equals(createTripRequestDto.getFromCity()) &&
            trip.getToCity().equals(createTripRequestDto.getToCity()) &&
            dateToLocalDate(trip.getArrivalDate()).equals(dateToLocalDate(createTripRequestDto.getArrivalDate()))) {
            return true;
        }
    }
    return false;
  }

  private LocalDate dateToLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public List<Trip> searchTrips(String query) {
    return tripDao.searchTrips(query);
  }

  @Override
  public boolean isUserTraveling(String userId) {
    List<Trip> tripList = tripDao.getTripsByShopperId(userId);
    List<TripResponseDto> tripResponseDtoList = getTripResponseDtoByTripList(tripList);

    return tripResponseDtoList.stream().anyMatch(
        tripResponseDto -> tripResponseDto.getTripStatus().equals(TripStatusEnum.ONGOING));
  }


  private TripStatusEnum getTripCollectionStatusBasedOnEachTripStatus(List<TripResponseDto> tripResponseDtoList) {


    // Check if any trip is ONGOING
    if (tripResponseDtoList.stream().anyMatch(tripResponseDto -> tripResponseDto.getTripStatus().equals(TripStatusEnum.ONGOING))) {
      return TripStatusEnum.ONGOING;
    } else if (tripResponseDtoList.stream().allMatch(tripResponseDto -> tripResponseDto.getTripStatus().equals(TripStatusEnum.UPCOMING))) {
      // Check if all trips are UPCOMING
      return TripStatusEnum.UPCOMING;
    }

    return TripStatusEnum.PAST;
  }

}
