package tw.pago.pagobackend.service.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.TripService;

@Component
public class TripServiceImpl implements TripService {

    @Autowired
    private TripDao tripDAO;

    @Override
    public Trip getTripById(Integer tripId) {
        return tripDAO.getTripById(tripId);
    }

//    @Override
//    public List<Trip> findAll() throws SQLException {
//        return tripDAO.findAll();
//    }

    @Override
    public Integer createTrip(CreateTripRequestDto createTripRequestDto) {
        return tripDAO.createTrip(createTripRequestDto);
    }

    @Override
    public void updateTrip(UpdateTripRequestDto updateTripRequestDto) {
        tripDAO.updateTrip(updateTripRequestDto);
    }

    @Override
    public void delete(Integer tripId) throws SQLException {
        tripDAO.delete(tripId);
    }

}
