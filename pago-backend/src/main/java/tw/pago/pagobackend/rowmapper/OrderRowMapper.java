package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
// import tw.pago.pagobackend.constant.PackagingEnum;
import tw.pago.pagobackend.model.Order;

public class OrderRowMapper implements RowMapper<Order> {

  @Override
  public Order mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Order order = Order.builder()
        .orderId(resultSet.getString("order_id"))
        .orderItemId(resultSet.getString("order_item_id"))
        .consumerId(resultSet.getString("consumer_id"))
        .createDate(resultSet.getTimestamp("create_date"))
        .isPackagingRequired(resultSet.getBoolean("packaging"))
        .isVerificationRequired(resultSet.getBoolean("verification"))
        .destination(resultSet.getString("destination"))
        .travelerFee(resultSet.getBigDecimal("traveler_fee"))
        .currency(CurrencyEnum.valueOf(resultSet.getString("currency")))
        .platformFeePercent(resultSet.getDouble("platform_fee_percent"))
        .tariffFeePercent(resultSet.getDouble("tariff_fee_percent"))
        .latestReceiveItemDate(resultSet.getTimestamp("latest_receive_item_date"))
        .note(resultSet.getString("note"))
        .orderStatus(OrderStatusEnum.valueOf(resultSet.getString("order_status")))
        .updateDate(resultSet.getTimestamp("update_date"))
        .build();

    // OrderItem
    // order.setName(resultSet.getString("name"));
// order.setImageUrl(resultSet.getString("image_url"));
// order.setDescription(resultSet.getString("description"));
// order.setQuantity(resultSet.getInt("quantity"));
// order.setUnitPrice(resultSet.getBigDecimal("unit_price"));
// order.setPurchaseLocation(resultSet.getString("purchase_location"));
    return order;
  }
}
