package tw.pago.pagobackend.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.rowmapper.TripRowMapper;

@Component
public class TripDaoImpl implements TripDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private DataSource dataSource;

  @Override
  public Trip getTripById(String tripId) {
    String sql = "SELECT trip_id, shopper_id, from_country, from_city, to_country, to_city, arrival_date, profit, create_date, update_date "
        + "FROM trip "
        + "WHERE trip_id = :tripId";

    Map<String, Object> map = new HashMap<>();
    map.put("tripId", tripId);

    List<Trip> tripList = namedParameterJdbcTemplate.query(sql, map, new TripRowMapper());

    if (tripList.size() > 0) {
      return tripList.get(0);
    } else {
      return null;
    }
  }

//    @Override
//    public List<Trip> findAll() throws SQLException {
//        List<Trip> trips = new ArrayList<Trip>();
//        String sql = "SELECT trip_id, traveler_id, from_location, to_location, arrival_date, profit FROM trip";
//        try (
//                Connection conn = dataSource.getConnection();
//                PreparedStatement stmt = conn.prepareStatement(sql);
//                ResultSet rs = stmt.executeQuery();) {
//            while (rs.next()) {
//                Trip trip = new Trip(0, 0, "", "", "", 0);
//                trip.setTripId(rs.getInt("trip_id"));
//                trip.setTravelerId(rs.getInt("traveler_id"));
//                trip.setFromLocation(rs.getString("from_location"));
//                trip.setToLocation(rs.getString("to_location"));
//                trip.setArrivalDate(rs.getString("arrival_date"));
//                trip.setProfit(rs.getDouble("profit"));
//                trips.add(trip);
//            }
//        } catch (SQLException e) {
//            throw e;
//        }
//        return trips;
//    }

  @Override
  public String createTrip(String userId, CreateTripRequestDto createTripRequestDto) {
    String sql =
        "INSERT INTO trip (trip_id, shopper_id, from_country, to_country, from_city, to_city, arrival_date, create_date, update_date) "
            + "VALUES (:tripId, :shopperId, :fromCountry, :toCountry, :fromCity, :toCity, :arrivalDate, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();
    map.put("shopperId", userId);
    map.put("tripId", createTripRequestDto.getTripId());
    map.put("fromCountry", createTripRequestDto.getFromCountry().name());
    map.put("toCountry", createTripRequestDto.getToCountry().name());
    map.put("fromCity", createTripRequestDto.getFromCity().name());
    map.put("toCity", createTripRequestDto.getToCity().name());
    map.put("arrivalDate", createTripRequestDto.getArrivalDate());
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    String tripId = createTripRequestDto.getTripId();
    return tripId;
  }


  @Override
  public void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto) {
    String sql = "UPDATE trip "
        + "SET shopper_id = :shopperId, from_country = :fromCountry, to_country = :toCountry, "
        + "from_city = :fromCity, to_city = :toCity, arrival_date = :arrivalDate, update_date = :updateDate "
        + "WHERE trip_id = :tripId";

    Map<String, Object> map = new HashMap<>();
    map.put("shopperId", updateTripRequestDto.getShopperId() != null ? updateTripRequestDto.getShopperId() : trip.getShopperId());
    map.put("fromCountry", updateTripRequestDto.getFromCountry() != null ? updateTripRequestDto.getFromCountry().name() : trip.getFromCountry().name());
    map.put("toCountry", updateTripRequestDto.getToCountry() != null ? updateTripRequestDto.getToCountry().name() : trip.getToCountry().name());
    map.put("fromCity", updateTripRequestDto.getFromCity() != null ? updateTripRequestDto.getFromCity().name() : trip.getFromCity().name());
    map.put("toCity", updateTripRequestDto.getToCity() != null ? updateTripRequestDto.getToCity().name() : trip.getToCity().name());
    map.put("arrivalDate", updateTripRequestDto.getArrivalDate() != null ? updateTripRequestDto.getArrivalDate() : trip.getArrivalDate());

    Date now = new Date();
    map.put("updateDate", now);
    map.put("tripId", updateTripRequestDto.getTripId());

    namedParameterJdbcTemplate.update(sql, map);
  }


  public void delete(String tripId) throws SQLException {
    String sql = "DELETE FROM trip WHERE trip_id=?";
    try (
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);) {
      stmt.setString(1, tripId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw e;
    }
  }


  @Override
  public List<Trip> getTripList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT trip_id, shopper_id, from_country, from_city, to_country, to_city, "
        + "arrival_date, profit, create_date, update_date "
        + "FROM trip "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());


    List<Trip> tripList = namedParameterJdbcTemplate.query(sql, map, new TripRowMapper());

    return tripList;
  }

  @Override
  public List<Trip> getMatchingTripListForOrder(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT trip_id, shopper_id, from_country, from_city, to_country, to_city, "
        + "arrival_date, profit, create_date, update_date "
        + "FROM trip "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. From, to, create_date
    sql = addMatchingTripForOrderSql(sql, map, listQueryParametersDto);


    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());


    List<Trip> matchingTripList = namedParameterJdbcTemplate.query(sql, map, new TripRowMapper());

    return matchingTripList;
  }


  @Override
  public Integer countTrip(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(trip_id) "
        + "FROM trip "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  @Override
  public Integer countTrip(TripStatusEnum tripStatus) {
    String sql = "SELECT COUNT(trip_id) "
        + "FROM trip "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    LocalDate currentDate = LocalDate.now();

    switch (tripStatus) {
      case UPCOMING:
        sql = sql + " AND DATE(arrival_date) > :currentDate  ";
        map.put("currentDate", currentDate);
        break;
      case ONGOING:
        sql = sql + " AND :currentDate BETWEEN DATE(arrival_date)  AND DATE_ADD(arrival_date, INTERVAL 7 DAY) ";
        map.put("currentDate", currentDate);
        break;
      case PAST:
        sql = sql + " AND DATE_ADD(arrival_date, INTERVAL 7 DAY) < :currentDate ";
        map.put("currentDate", currentDate);
        break;
      default:
        sql = sql;
        break;
    }

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  @Override
  public Integer countMatchingShopper(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(DISTINCT shopper_id) "
        + "FROM trip "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addMatchingTripForOrderSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {

    if (listQueryParametersDto.getUserId() != null) {
      sql = sql + " AND shopper_id = :shopperId ";
      map.put("shopperId", listQueryParametersDto.getUserId());
    }

    if (listQueryParametersDto.getOrderId() != null) {
      sql = sql + "AND order_id = orderId ";
    }

    if (listQueryParametersDto.getLatestReceiveItemDate() != null) {
      LocalDate latestReceiveItemDate = listQueryParametersDto.getLatestReceiveItemDate();
      sql = sql + " AND DATE(arrival_date) < :latestReceiveItemDate ";
      map.put("latestReceiveItemDate", latestReceiveItemDate);
    }

    if (listQueryParametersDto.getSearch() != null) {
      sql = sql + " "
          + "AND ("
          + "   from_country LIKE :search "
          + "OR from_city LIKE :search "
          + "OR to_country LIKE :search "
          + "OR to_city LIKE :search) ";
      map.put("search", "%" + listQueryParametersDto.getSearch() + "%");
    }

    return  sql;
  }

  private String addMatchingTripForOrderSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {

    if (listQueryParametersDto.getFromCity() != null) {
      sql = sql + " AND from_city = :fromCity ";
      map.put("fromCity", listQueryParametersDto.getFromCity().name());
    }

    if (listQueryParametersDto.getToCity() != null) {
      sql = sql + " AND to_city = :toCity ";
      map.put("toCity", listQueryParametersDto.getToCity().name());
    }

    sql = sql + " AND DATE(arrival_date) BETWEEN :orderCreateDate AND :latestReceiveItemDate ";
    map.put("latestReceiveItemDate", listQueryParametersDto.getLatestReceiveItemDate());
    map.put("orderCreateDate", listQueryParametersDto.getOrderCreateDate());


    return  sql;
  }
}
