package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.TripCollection;

public class TripCollectionRowMapper implements RowMapper<TripCollection> {

  @Override
  public TripCollection mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    TripCollection tripCollection = new TripCollection();
    tripCollection.setTripCollectionId(resultSet.getString("trip_collection_id"));
    tripCollection.setTripCollectionName(resultSet.getString("trip_collection_name"));
    tripCollection.setCreatorId(resultSet.getString("creator_id"));
    tripCollection.setCreateDate(resultSet.getTimestamp("create_date").toInstant().atZone(ZoneId.of("UTC")));
    tripCollection.setUpdateDate(resultSet.getTimestamp("update_date").toInstant().atZone(ZoneId.of("UTC")));
    return tripCollection;
  }
}
