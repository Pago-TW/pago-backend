package tw.pago.pagobackend.rowmapper;

import com.neovisionaries.i18n.CountryCode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.model.Trip;

public class TripRowMapper implements RowMapper<Trip> {

  @Override
  public Trip mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Trip trip = Trip.builder()
        .tripId(resultSet.getString("trip_id"))
        .shopperId(resultSet.getString("shopper_id"))
        .fromCountry(CountryCode.valueOf(resultSet.getString("from_country")))
        .fromCity(CityCode.valueOf(resultSet.getString("from_city")))
        .toCountry(CountryCode.valueOf(resultSet.getString("to_country")))
        .toCity(CityCode.valueOf(resultSet.getString("to_city")))
        .arrivalDate(resultSet.getTimestamp("arrival_date"))
        .profit(resultSet.getBigDecimal("profit"))
        .createDate(resultSet.getTimestamp("create_date"))
        .updateDate(resultSet.getTimestamp("update_date"))
        .build();

    return trip;
  }
}
