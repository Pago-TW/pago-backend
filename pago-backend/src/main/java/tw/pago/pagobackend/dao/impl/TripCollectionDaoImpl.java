package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.TripCollectionDao;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;

@Repository
@AllArgsConstructor
public class TripCollectionDaoImpl implements TripCollectionDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto) {
    String sql = "INSERT INTO trip_collection (trip_collection_id, trip_collection_name) "
        + "VALUES (:tripCollectionId, :tripCollectionName) ";

    Map<String, Object> map = new HashMap<>();
    map.put("tripCollectionId", createTripCollectionRequestDto.getTripCollectionId());
    map.put("tripCollectionName", createTripCollectionRequestDto.getTripCollectionName());

    namedParameterJdbcTemplate.update(sql, map);
  }
}
