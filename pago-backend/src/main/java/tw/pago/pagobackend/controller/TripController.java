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
    public ResponseEntity<Trip> get(@PathVariable Integer trip_id) throws SQLException {
        Trip trip = tripService.get(trip_id);
        if (trip != null) {
            return ResponseEntity.status(HttpStatus.OK).body(trip);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("trip")
    public ResponseEntity<List<Trip>> getList() throws SQLException {
        List<Trip> trips = tripService.getList();
        return ResponseEntity.status(HttpStatus.OK).body(trips);
    }

    @PostMapping("trip")
    public ResponseEntity<Trip> insert(@RequestBody Trip trip_request) throws SQLException {
        Integer trip_id = tripService.insert(trip_request);
        Trip trip = tripService.get(trip_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    @PutMapping("trip/{trip_id}")
    public ResponseEntity<Trip> update(@RequestBody Trip trip_request, @PathVariable Integer trip_id) throws SQLException {
        // check if trip exist
        Trip check_trip = tripService.get(trip_id);
        if (check_trip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // update trip
        tripService.update(trip_id, trip_request);
        Trip trip = tripService.get(trip_id);
        return ResponseEntity.status(HttpStatus.OK).body(trip);
    }

    @DeleteMapping("trip/{trip_id}")
    public ResponseEntity<?> delete(@PathVariable Integer trip_id) throws SQLException {
        tripService.delete(trip_id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
