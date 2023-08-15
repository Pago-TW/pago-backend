package tw.pago.pagobackend.dao;

import java.sql.SQLException;

import java.util.List;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;

public interface TripDao {
    Trip getTripById(String tripId);

//    public List<Trip> findAll() throws SQLException;

    String createTrip(String userId, CreateTripRequestDto createTripRequestDto);

    void createTrip(CreateTripRequestDto createTripRequestDto);

    void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto);

    void delete(String tripId) throws SQLException;

    List<Trip> getTripList(ListQueryParametersDto listQueryParametersDto);

    List<Trip> getTripsByShopperId(String shopperId);

    List<Trip> getTripListByTripStatus(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto);

    List<Trip> getMatchingTripListForOrder(ListQueryParametersDto listQueryParametersDto);

    List<Trip> getMatchingTripListWithRowNumberSqlForOrder(ListQueryParametersDto listQueryParametersDto);

    List<Trip> getTripListByTripColletionId(String tripCollectionId);

    Integer countTrip(ListQueryParametersDto listQueryParametersDto);

    Integer countTrip(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto);

    Integer countMatchingShopper(ListQueryParametersDto listQueryParametersDto);

    List<Trip> searchTrips(String query);
}
