package tw.pago.pagobackend.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dto.BatchCreateTripRequestDto;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripCollectionResponseDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.exception.AccessDeniedException;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.TripCollection;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@Validated
@AllArgsConstructor
public class TripController {

  private final TripService tripService;
  private final CurrentUserInfoProvider currentUserInfoProvider;

  @GetMapping("/trips/{tripId}")
  public ResponseEntity<TripResponseDto> getTripById(@PathVariable String tripId) {
    Trip trip = tripService.getTripById(tripId);
    TripResponseDto tripResponseDto = tripService.getTripResponseDtoByTrip(trip);

    if (trip != null) {
      return ResponseEntity.status(HttpStatus.OK).body(tripResponseDto);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  // @GetMapping("trip")
  // public ResponseEntity<List<Trip>> findAll() throws SQLException {
  // List<Trip> trips = tripService.findAll();
  // return ResponseEntity.status(HttpStatus.OK).body(trips);
  // }

  @PostMapping("/trips") // TODO 抵達時間不該在今天以前，不合理
  // TODO 先不要做 ^
  public ResponseEntity<Trip> createTrip(
      @RequestBody @Valid CreateTripRequestDto createTripRequestDto) {

    String tripId = tripService.createTrip(currentUserInfoProvider.getCurrentLoginUserId(),
        createTripRequestDto);
    Trip trip = tripService.getTripById(tripId);

    return ResponseEntity.status(HttpStatus.CREATED).body(trip);

  }

  @PostMapping("/trips/batch")
  public ResponseEntity<TripCollection> batchCreateTrip(
      @RequestBody @Valid BatchCreateTripRequestDto batchCreateTripRequestDto) {

    batchCreateTripRequestDto.setShopperId(currentUserInfoProvider.getCurrentLoginUserId());
    TripCollection tripCollection = tripService.batchCreateTrip(batchCreateTripRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(tripCollection);
  }

  @PatchMapping("/users/{userId}/trips/{tripId}")
  public ResponseEntity<Trip> updateTrip(@PathVariable String userId,
      @PathVariable String tripId,
      @RequestBody @Valid UpdateTripRequestDto updateTripRequestDto) {

    // Check if the Trip to be updated exists
    Trip trip = tripService.getTripById(tripId);
    if (trip == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(trip);
    }
    // Update Trip
    updateTripRequestDto.setShopperId(userId);
    updateTripRequestDto.setTripId(tripId);
    tripService.updateTrip(trip, updateTripRequestDto);
    Trip updatedTrip = tripService.getTripById(tripId);

    return ResponseEntity.status(HttpStatus.OK).body(updatedTrip);
  }

  @DeleteMapping("/trips/{tripId}")
  public ResponseEntity<String> delete(@PathVariable String tripId)
      throws SQLException {

    Trip trip = tripService.getTripById(tripId);
    String tripCreatorId = trip.getShopperId();

    // Permission Checking
    if (!currentUserInfoProvider.getCurrentLoginUserId().equals(tripCreatorId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    try {
      tripService.deleteTripByTrip(trip);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

  }

  @GetMapping("/trips")
  public ResponseEntity<Object> getTripList(
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String orderId,
      // Filter trips by the latest date to receive items (optional); only include
      // trips with an arrival_date before this date
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate latestReceiveItemDate,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) TripStatusEnum status,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "25") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // If an order ID is provided, fetch matching trips for that order
    if (orderId != null) {
      ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
          .userId(currentLoginUserId)
          .startIndex(startIndex)
          .size(size)
          .orderBy(orderBy)
          .sort(sort)
          .build();

      List<Trip> tripList = tripService.getMatchingTripListByOrderId(orderId,
          listQueryParametersDto);
      List<TripResponseDto> tripResponseDtoList = tripService.getTripResponseDtoByTripList(
          tripList);

      return ResponseEntity.status(HttpStatus.OK).body(tripResponseDtoList);
    }

    // If orderId is null
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId == null ? currentLoginUserId : userId)
        .latestReceiveItemDate(latestReceiveItemDate)
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .tripStatus(status)
        .sort(sort)
        .build();

    // Initialize variables for total count and trip response DTO list
    Integer total = 0;
    List<TripResponseDto> tripResponseDtoList;

    // If a trip status is provided, filter trips by the given status
    if (listQueryParametersDto.getTripStatus() != null) {
      TripStatusEnum tripStatus = listQueryParametersDto.getTripStatus();

      List<Trip> tripList = tripService.getTripListByTripStatus(tripStatus, listQueryParametersDto);
      tripResponseDtoList = tripService.getTripResponseDtoByTripList(tripList);

      total = tripService.countTrip(tripStatus, listQueryParametersDto);
    } else {
      // If no trip status is provided, fetch all trips based on the query parameters
      tripResponseDtoList = tripService.getTripResponseDtoList(listQueryParametersDto);
      total = tripService.countTrip(listQueryParametersDto);
    }

    // Build the pagination response DTO
    ListResponseDto<TripResponseDto> tripResponseDtoListResponseDto = ListResponseDto.<TripResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(tripResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(tripResponseDtoListResponseDto);
  }

  @GetMapping("/trip-collections")
  public ResponseEntity<ListResponseDto<TripCollectionResponseDto>> getTripCollectionList(
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "25") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "arrival_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort,
      @RequestParam(required = false) String search) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(currentLoginUserId)
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .sort(sort)
        .build();

    // Check the orderBy parameter is existed in MySQL table
    if ("create_date".equals(orderBy) || "update_date".equals(orderBy)) {
      // Use SQL to sort by create_date or update_date
      listQueryParametersDto.setOrderBy(orderBy);
    } else {
      listQueryParametersDto.setOrderBy(null);
    }

    List<TripCollection> tripCollectionList = tripService.getTripCollectionList(
        listQueryParametersDto);
    List<TripCollectionResponseDto> tripCollectionResponseDtoList = tripService
        .getTripCollectionResponseDtoListByTripCollectionList(
            tripCollectionList);

    List<TripCollectionResponseDto> sortedTripCollectionResponseDtoList = tripService.sortTripCollectionResponseDtoList(
        tripCollectionResponseDtoList, sort, orderBy);

    int total = tripService.countTripCollection(listQueryParametersDto);

    // Build the pagination response DTO
    ListResponseDto<TripCollectionResponseDto> tripResponseDtoListResponseDto = ListResponseDto
        .<TripCollectionResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(sortedTripCollectionResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(tripResponseDtoListResponseDto);

  }

  @GetMapping("/trips/{tripId}/matching-orders")
  public ResponseEntity<ListResponseDto<OrderResponseDto>> getMatchingOrderListForTrip(
      @PathVariable String tripId,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "REQUESTED") OrderStatusEnum orderStatus,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "traveler_fee") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()

        .search(search)
        .orderStatus(orderStatus)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    Trip trip = tripService.getTripById(tripId);

    List<OrderResponseDto> matchingOrderResponseDtoListForTrip = tripService.getMatchingOrderResponseDtoListForTrip(
        listQueryParametersDto, trip);

    Integer total = tripService.countMatchingOrderForTrip(listQueryParametersDto, trip);

    ListResponseDto<OrderResponseDto> listResponseDto = ListResponseDto.<OrderResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(matchingOrderResponseDtoListForTrip)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(listResponseDto);

  }

}
