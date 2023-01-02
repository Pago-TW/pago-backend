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
    public Trip getById(Integer tripId) throws SQLException {
        return tripDAO.getById(tripId);
    }

    @Override
    public List<Trip> findAll() throws SQLException {
        return tripDAO.findAll();
    }

    @Override
    public Integer insert(Trip tripRequest) throws SQLException {
        return tripDAO.insert(tripRequest);
    }

    @Override
    public void update(Integer tripId, Trip tripRequest) throws SQLException {
        tripDAO.update(tripId, tripRequest);
    }

    @Override
    public void delete(Integer tripId) throws SQLException {
        tripDAO.delete(tripId);
    }

}
