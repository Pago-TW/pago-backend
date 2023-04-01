package tw.pago.pagobackend.service;

import java.sql.SQLException;

import java.util.List;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;

public interface TripService {
    
    Trip getTripById(String tripId);

    TripResponseDto getTripResponseDtoByTrip(Trip trip);

//    public List<Trip> findAll() throws SQLException;

    String createTrip(String userId, CreateTripRequestDto createTripRequestDto);

    void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto);

    void deleteTripById(String tripId) throws SQLException;

    List<Trip> getTripList(ListQueryParametersDto listQueryParametersDto);

    Integer countTrip(ListQueryParametersDto listQueryParametersDto);
}
