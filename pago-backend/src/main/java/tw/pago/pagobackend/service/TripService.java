package tw.pago.pagobackend.service;

import java.sql.SQLException;

import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;

public interface TripService {
    
    Trip getTripById(Integer tripId);

//    public List<Trip> findAll() throws SQLException;

    Integer createTrip(CreateTripRequestDto createTripRequestDto);

    void updateTrip(UpdateTripRequestDto updateTripRequestDto);

    void deleteTripById(Integer tripId) throws SQLException;
}
