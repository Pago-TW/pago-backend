package tw.pago.pagobackend.dao;

import java.sql.SQLException;

import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.model.Trip;

public interface TripDao {
    public Trip getTripById(Integer tripId);

//    public List<Trip> findAll() throws SQLException;

    public Integer createTrip(CreateTripRequestDto createTripRequestDto);

//    public void update(Integer tripId, Trip tripRequest) throws SQLException;

    public void delete(Integer tripId) throws SQLException;
}
