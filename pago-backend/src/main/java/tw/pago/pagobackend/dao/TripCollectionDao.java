package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;

public interface TripCollectionDao {

  void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto);
}
