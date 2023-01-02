package tw.pago.pagobackend.service;

import java.sql.SQLException;
import java.util.List;

import tw.pago.pagobackend.model.Trip;

public interface TripService {
    
    public Trip getById(Integer tripId) throws SQLException;

    public List<Trip> findAll() throws SQLException;

    public Integer insert(Trip tripRequest) throws SQLException;

    public void update(Integer tripId, Trip tripRequest) throws SQLException;

    public void delete(Integer tripId) throws SQLException;
}
