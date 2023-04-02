package tw.pago.pagobackend.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
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
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@Validated
public class TripController {

  @Autowired
  private TripService tripService;
  @Autowired
  private OrderService orderService;
  @Autowired
  private CurrentUserInfoProvider currentUserInfoProvider;

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

//  @GetMapping("trip")
//  public ResponseEntity<List<Trip>> findAll() throws SQLException {
//    List<Trip> trips = tripService.findAll();
//    return ResponseEntity.status(HttpStatus.OK).body(trips);
//  }

  @PostMapping("/trips")
  public ResponseEntity<Trip> createTrip(@RequestBody @Valid CreateTripRequestDto createTripRequestDto) throws SQLException {

    String tripId = tripService.createTrip(currentUserInfoProvider.getCurrentLoginUserId(), createTripRequestDto);
    Trip trip = tripService.getTripById(tripId);

    return ResponseEntity.status(HttpStatus.CREATED).body(trip);

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
  public ResponseEntity<?> delete(@PathVariable String tripId)
      throws SQLException {

    Trip trip = tripService.getTripById(tripId);
    String tripCreatorId = trip.getShopperId();
    if (!currentUserInfoProvider.getCurrentLoginUserId().equals(tripCreatorId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    tripService.deleteTripById(tripId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/users/{userId}/trips")
  public ResponseEntity<ListResponseDto<TripResponseDto>> getTripList(
      @PathVariable String userId,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate latestReceiveItemDate,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId)
        .latestReceiveItemDate(latestReceiveItemDate)
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    List<TripResponseDto> tripList = tripService.getTripResponseDtoList(listQueryParametersDto);

    Integer total = tripService.countTrip(listQueryParametersDto);

    ListResponseDto<TripResponseDto> tripResponseDtoList = ListResponseDto.<TripResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(tripList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(tripResponseDtoList);
  }


  @GetMapping("/trips/{tripId}/matching-orders")
  public ResponseEntity<ListResponseDto<OrderResponseDto>> getMatchingOrderListForTrip(
      @PathVariable String tripId,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate latestReceiveItemDate,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "REQUESTED") OrderStatusEnum orderStatus,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "latest_receive_item_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .tripId(tripId)
        .latestReceiveItemDate(latestReceiveItemDate)
        .search(search)
        .orderStatus(orderStatus)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    Trip trip = tripService.getTripById(tripId);

    List<OrderResponseDto> matchingOrderResponseDtoListForTrip = tripService.getMatchingOrderResponseDtoListForTrip(listQueryParametersDto, trip);

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
