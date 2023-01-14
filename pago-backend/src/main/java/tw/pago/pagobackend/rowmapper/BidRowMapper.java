package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.tree.TreePath;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.model.Bid;

public class BidRowMapper implements RowMapper<Bid> {

  @Override
  public Bid mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Bid bid = new Bid();
    bid.setBidId(resultSet.getInt("bid_id"));
    bid.setOrderId(resultSet.getInt("order_id"));
    bid.setTripId(resultSet.getInt("trip_id"));
    bid.setBidAmount(resultSet.getBigDecimal("bid_amount"));
    bid.setCurrency(CurrencyEnum.valueOf(resultSet.getString("currency")));
    bid.setCreateDate(resultSet.getTimestamp("create_date"));

    return bid;
  }
}
