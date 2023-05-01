package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Trip;

public class BidWithTripRowMapper implements RowMapper<Bid> { // TODO 我忘了為什麼我會命名成 BidWithTrip == 但這看起來就是普通的BidRowMapper
                                                              // TODO by dayc: 我測試過了，這兩個一模一樣

  @Override
  public Bid mapRow(ResultSet resultSet, int rowNum) throws SQLException {

    Bid bid = Bid.builder()
        .bidId(resultSet.getString("bid_id"))
        .orderId(resultSet.getString("order_id"))
        .tripId(resultSet.getString("trip_id"))
        .bidAmount(resultSet.getBigDecimal("bid_amount"))
        .currency(CurrencyEnum.valueOf(resultSet.getString("currency")))
        .createDate(resultSet.getTimestamp("create_date"))
        .updateDate(resultSet.getTimestamp("update_date"))
        .latestDeliveryDate(resultSet.getTimestamp("latest_delivery_date"))
        .bidStatus(BidStatusEnum.valueOf(resultSet.getString("bid_status")))
        .build();

    return bid;
  }
}
