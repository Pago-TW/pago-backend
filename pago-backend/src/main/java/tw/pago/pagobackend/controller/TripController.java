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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.TripService;

@RestController
public class TripController {

  @Autowired
  private TripService tripService;

  @GetMapping("/trips/{tripId}")
  public ResponseEntity<Trip> getTripById(@PathVariable String tripId) {
    Trip trip = tripService.getTripById(tripId);

    if (trip != null) {
      return ResponseEntity.status(HttpStatus.OK).body(trip);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

//  @GetMapping("trip")
//  public ResponseEntity<List<Trip>> findAll() throws SQLException {
//    List<Trip> trips = tripService.findAll();
//    return ResponseEntity.status(HttpStatus.OK).body(trips);
//  }

  @PostMapping("/users/{userId}/trips")
  public ResponseEntity<Trip> createTrip(@PathVariable String userId,
    @RequestBody @Valid CreateTripRequestDto createTripRequestDto) throws SQLException {

    String tripId = tripService.createTrip(userId, createTripRequestDto);
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

  @DeleteMapping("users/{userId}/trips/{tripId}")
  public ResponseEntity<?> delete(@PathVariable String userId, @PathVariable String tripId)
      throws SQLException {
    tripService.deleteTripById(tripId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/users/{userId}/trips")
  public ResponseEntity<ListResponseDto<Trip>> getTripList(
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

    List<Trip> tripList = tripService.getTripList(listQueryParametersDto);

    Integer total = tripService.countTrip(listQueryParametersDto);

    ListResponseDto<Trip> tripListResponseDto = ListResponseDto.<Trip>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(tripList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(tripListResponseDto);
  }
}
