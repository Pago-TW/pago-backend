package tw.pago.pagobackend.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
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
    map.put("fromCountry", createTripRequestDto.getFromCountry());
    map.put("toCountry", createTripRequestDto.getToCountry());
    map.put("fromCity", createTripRequestDto.getFromCity());
    map.put("toCity", createTripRequestDto.getToCity());
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
    map.put("fromCountry", updateTripRequestDto.getFromCountry() != null ? updateTripRequestDto.getFromCountry() : trip.getFromCountry());
    map.put("toCountry", updateTripRequestDto.getToCountry() != null ? updateTripRequestDto.getToCountry() : trip.getToCountry());
    map.put("fromCity", updateTripRequestDto.getFromCity() != null ? updateTripRequestDto.getFromCity() : trip.getFromCity());
    map.put("toCity", updateTripRequestDto.getToCity() != null ? updateTripRequestDto.getToCity() : trip.getToCity());
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
}
