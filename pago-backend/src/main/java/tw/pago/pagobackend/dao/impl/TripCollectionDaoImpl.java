package tw.pago.pagobackend.dao.impl;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.TripCollectionDao;
import tw.pago.pagobackend.dto.CreateTripCollectionRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.TripCollection;
import tw.pago.pagobackend.rowmapper.TripCollectionRowMapper;

@Repository
@AllArgsConstructor
public class TripCollectionDaoImpl implements TripCollectionDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createTripCollection(CreateTripCollectionRequestDto createTripCollectionRequestDto) {
    String sql = "INSERT INTO trip_collection (trip_collection_id, creator_id, trip_collection_name, create_date, update_date) "
        + "VALUES (:tripCollectionId, :creatorId, :tripCollectionName, :createDate, :updateDate) ";

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

    Map<String, Object> map = new HashMap<>();
    map.put("tripCollectionId", createTripCollectionRequestDto.getTripCollectionId());
    map.put("creatorId", createTripCollectionRequestDto.getCreatorId());
    map.put("tripCollectionName", createTripCollectionRequestDto.getTripCollectionName());
    map.put("createDate", Timestamp.from(now.toInstant()));
    map.put("updateDate", Timestamp.from(now.toInstant()));



    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public TripCollection getTripCollectionById(String tripCollectionId) {
    String sql = "SELECT trip_collection_id, trip_collection_name, creator_id, create_date, update_date "
        + "FROM trip_collection "
        + "WHERE trip_collection_id = :tripCollectionId ";


    Map<String, Object> map = new HashMap<>();
    map.put("tripCollectionId", tripCollectionId);

    List<TripCollection> tripCollectionList = namedParameterJdbcTemplate.query(sql, map, new TripCollectionRowMapper());

    if (!tripCollectionList.isEmpty()) {
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


    return namedParameterJdbcTemplate.query(sql, map, new TripCollectionRowMapper());
  }

  @Override
  public List<TripCollection> getTripCollectionList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT trip_collection_id, trip_collection_name, creator_id, create_date, update_date "
        + "FROM trip_collection "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order and Sort
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());



    return namedParameterJdbcTemplate.query(sql, map, new TripCollectionRowMapper());
  }

  @Override
  public Integer countTripCollection(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(trip_collection_id) "
        + "FROM trip_collection "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }


  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {

    if (listQueryParametersDto.getUserId() != null) {
      sql = sql + " AND creator_id = :creatorId ";
      map.put("creatorId", listQueryParametersDto.getUserId());
    }


    if (listQueryParametersDto.getSearch() != null && !listQueryParametersDto.getSearch().isBlank()) {
      sql = sql + " AND ("
          + "trip_collection_name LIKE :search "
          + "OR trip_collection_id IN ("
          + "SELECT trip_collection_id FROM trip WHERE "
          + "from_country LIKE :search "
          + "OR from_city LIKE :search "
          + "OR to_country LIKE :search "
          + "OR to_city LIKE :search"
          + ")) ";
      map.put("search", "%" + listQueryParametersDto.getSearch() + "%");
    }


    return sql;
  }
}
