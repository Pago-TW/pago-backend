package tw.pago.pagobackend.controller;

import java.sql.SQLException;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.TripService;

@RestController
public class TripController {

  @Autowired
  private TripService tripService;

  @GetMapping("/trip/{tripId}")
  public ResponseEntity<Trip> getTripById(@PathVariable Integer tripId) {
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
  public ResponseEntity<Trip> createTrip(@PathVariable Integer userId,
      @RequestBody @Valid CreateTripRequestDto createTripRequestDto) throws SQLException {

    createTripRequestDto.setTravelerId(userId);
    Integer tripId = tripService.createTrip(createTripRequestDto);
    Trip trip = tripService.getTripById(tripId);

    return ResponseEntity.status(HttpStatus.CREATED).body(trip);

  }

//  @PutMapping("trip/{trip_id}")
//  public ResponseEntity<Trip> update(@RequestBody Trip tripRequest,
//      @PathVariable("trip_id") Integer tripId)
//      throws SQLException {
//    // check if trip exist
//    Trip checkTrip = tripService.getTripById(tripId);
//    if (checkTrip == null) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//    }

    // update trip
//    tripService.update(tripId, tripRequest);
//    Trip trip = tripService.getTripById(tripId);
//    return ResponseEntity.status(HttpStatus.OK).body(trip);
//  }

  @DeleteMapping("user/{userId}/trip/{tripId}")
  public ResponseEntity<?> delete(@PathVariable Integer userId, @PathVariable Integer tripId) throws SQLException {
    tripService.delete(tripId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
