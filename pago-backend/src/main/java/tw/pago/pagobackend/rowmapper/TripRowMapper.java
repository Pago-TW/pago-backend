package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.Trip;

public class TripRowMapper implements RowMapper<Trip> {

  @Override
  public Trip mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Trip trip = new Trip();
    trip.setTripId(resultSet.getString("trip_id"));
    trip.setTravelerId(resultSet.getString("traveler_id"));
    trip.setFromCountry(resultSet.getString("from_location"));
    trip.setFromCity(resultSet.getString("from_city"));
    trip.setToCountry(resultSet.getString("to_location"));
    trip.setToCity(resultSet.getString("to_city"));
    trip.setArrivalDate(resultSet.getTimestamp("arrival_date"));
    trip.setProfit(resultSet.getBigDecimal("profit"));
    trip.setCreateDate(resultSet.getTimestamp("create_date"));
    trip.setUpdateDate(resultSet.getTimestamp("update_date"));

    return trip;
  }
}
