package tw.pago.pagobackend.service;

import java.sql.SQLException;

import java.util.List;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dto.BatchCreateTripRequestDto;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripCollectionResponseDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.TripCollection;
import tw.pago.pagobackend.model.User;

public interface TripService {

    Trip getTripById(String tripId);

    TripCollection getTripCollectionById(String tripCollectionId);

    TripResponseDto getTripResponseDtoByTrip(Trip trip);

    TripCollectionResponseDto getTripCollectionResponseDtoByTripCollection(
        TripCollection tripCollection);

    User getUserByShopperId(String shopperId);

    String createTrip(String userId, CreateTripRequestDto createTripRequestDto);

    void createTrip(CreateTripRequestDto createTripRequestDto);

    TripCollection batchCreateTrip(BatchCreateTripRequestDto batchCreateTripRequestDto);

    void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto);

    void deleteTripById(String tripId) throws SQLException;

    void deleteTripByTrip(Trip trip) throws SQLException;

    void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto);

    List<Trip> getTripList(ListQueryParametersDto ListQueryParametersDto);

    List<Trip> getTripsByShopperId(String shopperId);

    List<Trip> getTripListByTripColletionId(String tripCollectionId);

    List<Trip> getMatchingTripListByOrderId(String orderId, ListQueryParametersDto listQueryParametersDto);

    List<Trip> getTripListByTripStatus(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto);

    List<TripCollection> getTripCollectionList(ListQueryParametersDto listQueryParametersDto);

    List<TripCollection> getTripCollectionListByCreatorId(String creatorId);

    List<OrderResponseDto> getMatchingOrderResponseDtoListForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

    List<TripResponseDto> getTripResponseDtoList(ListQueryParametersDto listQueryParametersDto);

    List<TripResponseDto> getTripResponseDtoByTripList(List<Trip> tripList);

    List<TripCollectionResponseDto> getTripCollectionResponseDtoListByTripCollectionList(List<TripCollection> tripCollectionList);

    Integer countTrip(ListQueryParametersDto listQueryParametersDto);

    Integer countTrip(TripStatusEnum tripStatus, ListQueryParametersDto listQueryParametersDto);

    Integer countTripCollection(ListQueryParametersDto listQueryParametersDto);

    Integer countMatchingShopper(ListQueryParametersDto listQueryParametersDto);

    Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto, Trip trip);

    List<Trip> searchTrips(String query);
}
