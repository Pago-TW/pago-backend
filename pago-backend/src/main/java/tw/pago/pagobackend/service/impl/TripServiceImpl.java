package tw.pago.pagobackend.service.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class TripServiceImpl implements TripService {

    @Autowired
    private TripDao tripDAO;
    @Autowired
    private UuidGenerator uuidGenerator;

    @Override
    public Trip getTripById(String tripId) {
        return tripDAO.getTripById(tripId);
    }

//    @Override
//    public List<Trip> findAll() throws SQLException {
//        return tripDAO.findAll();
//    }

    @Override
    public String createTrip(String userId, CreateTripRequestDto createTripRequestDto) {
        String tripUuid = uuidGenerator.getUuid();
        createTripRequestDto.setTripId(tripUuid);
        return tripDAO.createTrip(userId, createTripRequestDto);
    }

    @Override
    public void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto) {
        tripDAO.updateTrip(trip, updateTripRequestDto);
    }

    @Override
    public void deleteTripById(String tripId) throws SQLException {
        tripDAO.delete(tripId);
    }

}
