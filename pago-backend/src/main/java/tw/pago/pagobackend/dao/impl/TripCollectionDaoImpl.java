package tw.pago.pagobackend.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.TripCollectionDao;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;
import tw.pago.pagobackend.model.TripCollection;
import tw.pago.pagobackend.rowmapper.TripColletionRowMapper;

@Repository
@AllArgsConstructor
public class TripCollectionDaoImpl implements TripCollectionDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto) {
    String sql = "INSERT INTO trip_collection (trip_collection_id, creator_id, trip_collection_name) "
        + "VALUES (:tripCollectionId, :creatorId, :tripCollectionName) ";

    Map<String, Object> map = new HashMap<>();
    map.put("tripCollectionId", createTripCollectionRequestDto.getTripCollectionId());
    map.put("creatorId", createTripCollectionRequestDto.getCreatorId());
    map.put("tripCollectionName", createTripCollectionRequestDto.getTripCollectionName());

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public TripCollection getTripCollectionById(String tripCollectionId) {
    String sql = "SELECT trip_collection_id, trip_collection_name, creator_id "
        + "FROM trip_collection "
        + "WHERE trip_collection_id = :tripCollectionId ";

    Map<String, Object> map = new HashMap<>();
    map.put("tripCollectionId", tripCollectionId);

    List<TripCollection> tripCollectionList = namedParameterJdbcTemplate.query(sql, map, new TripColletionRowMapper());

    if (tripCollectionList.size() > 0) {
      return tripCollectionList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<TripCollection> getTripCollectionListByCreatorId(String creatorId) {
    String sql = "SELECT trip_collection_id, trip_collection_name, creator_id "
        + "FROM trip_collection "
        + "WHERE creator_id = :creatorId ";

    Map<String, Object> map = new HashMap<>();
    map.put("creatorId", creatorId);

    List<TripCollection> tripCollectionList = namedParameterJdbcTemplate.query(sql, map, new TripColletionRowMapper());

    return tripCollectionList;
  }
}
