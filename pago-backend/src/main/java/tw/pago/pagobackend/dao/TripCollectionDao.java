package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.TripCollection;

public interface TripCollectionDao {

  void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto);

  TripCollection getTripCollectionById(String tripCollectionId);

  List<TripCollection> getTripCollectionListByCreatorId(String creatorId);

  List<TripCollection> getTripCollectionList(ListQueryParametersDto listQueryParametersDto);

  Integer countTripCollection(ListQueryParametersDto listQueryParametersDto);

  void deleteTripCollectionById(String tripCollectionId);
}
