package tw.pago.pagobackend.dao;

import java.sql.SQLException;

import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;

public interface TripDao {
    Trip getTripById(Integer tripId);

//    public List<Trip> findAll() throws SQLException;

    Integer createTrip(CreateTripRequestDto createTripRequestDto);

    void updateTrip(UpdateTripRequestDto updateTripRequestDto);

    void delete(Integer tripId) throws SQLException;
}
