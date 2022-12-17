package tw.pago.pagobackend.service;

import java.sql.SQLException;
import java.util.List;

import tw.pago.pagobackend.entity.Trip;

public interface TripService {
    
    public Trip get(Integer trip_id) throws SQLException;

    public List<Trip> getList() throws SQLException;

    public Integer insert(Trip trip_request) throws SQLException;

    public void update(Integer trip_id, Trip trip_request) throws SQLException;

    public void delete(Integer trip_id) throws SQLException;
}
