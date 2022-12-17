package tw.pago.pagobackend.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tw.pago.pagobackend.dao.TripDAO;
import tw.pago.pagobackend.entity.Trip;
import tw.pago.pagobackend.service.TripService;

@Component
public class TripServiceImpl implements TripService {

    @Autowired
    private TripDAO tripDAO;

    @Override
    public Trip get(Integer trip_id) throws SQLException {
        return tripDAO.get(trip_id);
    }

    @Override
    public List<Trip> getList() throws SQLException {
        return tripDAO.getList();
    }

    @Override
    public Integer insert(Trip trip_request) throws SQLException {
        return tripDAO.insert(trip_request);
    }

    @Override
    public void update(Integer trip_id, Trip trip_request) throws SQLException {
        tripDAO.update(trip_id, trip_request);
    }

    @Override
    public void delete(Integer trip_id) throws SQLException {
        tripDAO.delete(trip_id);
    }

}
