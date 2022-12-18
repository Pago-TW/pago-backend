package tw.pago.pagobackend.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.pago.pagobackend.entity.Trip;
import tw.pago.pagobackend.service.TripService;

@RestController
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping("trip/{trip_id}")
    public ResponseEntity<Trip> getById(@PathVariable("trip_id") Integer tripId) throws SQLException {
        Trip trip = tripService.getById(tripId);
        if (trip != null) {
            return ResponseEntity.status(HttpStatus.OK).body(trip);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("trip")
    public ResponseEntity<List<Trip>> findAll() throws SQLException {
        List<Trip> trips = tripService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(trips);
    }

    @PostMapping("trip")
    public ResponseEntity<Trip> insert(@RequestBody Trip tripRequest) throws SQLException {
        Integer tripId = tripService.insert(tripRequest);
        Trip trip = tripService.getById(tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    @PutMapping("trip/{trip_id}")
    public ResponseEntity<Trip> update(@RequestBody Trip tripRequest, @PathVariable("trip_id") Integer tripId) throws SQLException {
        // check if trip exist
        Trip checkTrip = tripService.getById(tripId);
        if (checkTrip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // update trip
        tripService.update(tripId, tripRequest);
        Trip trip = tripService.getById(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(trip);
    }

    @DeleteMapping("trip/{trip_id}")
    public ResponseEntity<?> delete(@PathVariable("trip_id") Integer tripId) throws SQLException {
        tripService.delete(tripId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
