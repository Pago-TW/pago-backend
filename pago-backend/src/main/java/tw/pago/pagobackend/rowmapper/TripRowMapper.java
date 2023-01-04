package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.Trip;

public class TripRowMapper implements RowMapper<Trip> {

  @Override
  public Trip mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Trip trip = new Trip();
    trip.setTripId(resultSet.getInt("trip_id"));
    trip.setTravelerId(resultSet.getInt("traveler_id"));
    trip.setFromLocation(resultSet.getString("from_location"));
    trip.setToLocation(resultSet.getString("to_location"));
    trip.setArrivalDate(resultSet.getTimestamp("arrival_date"));
    trip.setProfit(resultSet.getBigDecimal("profit"));
    trip.setCreateDate(resultSet.getTimestamp("create_date"));
    trip.setUpdateDate(resultSet.getTimestamp("update_date"));

    return trip;
  }
}
