package tw.pago.pagobackend.service;

import java.sql.SQLException;

import java.util.List;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;

public interface TripService {

    Trip getTripById(String tripId);

    TripResponseDto getTripResponseDtoByTrip(Trip trip);

    User getUserByShopperId(String shopperId);

    // public List<Trip> findAll() throws SQLException;

    String createTrip(String userId, CreateTripRequestDto createTripRequestDto);

    void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto);

    void deleteTripById(String tripId) throws SQLException;

    void deleteTripByTrip(Trip trip) throws SQLException;

    List<Trip> getTripList(ListQueryParametersDto ListQueryParametersDto);

    List<Trip> getMatchingTripListByOrderId(String orderId, ListQueryParametersDto listQueryParametersDto);

    List<Trip> getTripListByTripStatus(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto);

    List<OrderResponseDto> getMatchingOrderResponseDtoListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

    List<TripResponseDto> getTripResponseDtoList(ListQueryParametersDto listQueryParametersDto);

    List<TripResponseDto> getTripResponseDtoByTripList(List<Trip> tripList);

    Integer countTrip(ListQueryParametersDto listQueryParametersDto);

    Integer countTrip(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto);

    Integer countMatchingShopper(ListQueryParametersDto listQueryParametersDto);

    Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);
}
