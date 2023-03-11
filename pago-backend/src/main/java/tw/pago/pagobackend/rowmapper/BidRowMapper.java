package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.model.Bid;

public class BidRowMapper implements RowMapper<Bid> {

  @Override
  public Bid mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Bid bid = new Bid();
    bid.setBidId(resultSet.getString("bid_id"));
    bid.setOrderId(resultSet.getString("order_id"));
    bid.setTripId(resultSet.getString("trip_id"));

    bid.setBidAmount(resultSet.getBigDecimal("bid_amount"));
    bid.setCurrency(CurrencyEnum.valueOf(resultSet.getString("currency")));
    bid.setCreateDate(resultSet.getTimestamp("create_date"));
    bid.setUpdateDate(resultSet.getTimestamp("update_date"));
    bid.setBidStatus(BidStatusEnum.valueOf(resultSet.getString("bid_status")));

    return bid;
  }
}
