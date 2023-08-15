package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;
import tw.pago.pagobackend.model.TripCollection;

public interface TripCollectionDao {

  void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto);

  TripCollection getTripCollectionById(String tripCollectionId);

  List<TripCollection> getTripCollectionListByCreatorId(String creatorId);
}
