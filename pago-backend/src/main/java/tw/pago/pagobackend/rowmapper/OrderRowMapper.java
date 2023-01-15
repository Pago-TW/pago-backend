package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.PackagingEnum;
import tw.pago.pagobackend.model.Order;

public class OrderRowMapper implements RowMapper<Order> {

  @Override
  public Order mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Order order = new Order();
    order.setOrderId(resultSet.getString("order_id"));
    order.setOrderItemId(resultSet.getString("order_item_id"));
    order.setShopperId(resultSet.getInt("shopper_id"));
    order.setCreateDate(resultSet.getTimestamp("create_date"));
    order.setPackaging(PackagingEnum.valueOf(resultSet.getString("packaging")));
    order.setVerification(resultSet.getString("verification"));
    order.setDestination(resultSet.getString("destination"));
    order.setTravelerFee(resultSet.getBigDecimal("traveler_fee"));
    order.setCurrency(CurrencyEnum.valueOf(resultSet.getString("currency")));
    order.setPlatformFeePercent(resultSet.getDouble("platform_fee_percent"));
    order.setTariffFeePercent(resultSet.getDouble("tariff_fee_percent"));
    order.setLatestReceiveItemDate(resultSet.getTimestamp("latest_receive_item_date"));
    order.setNote(resultSet.getString("note"));
    order.setOrderStatus(OrderStatusEnum.valueOf(resultSet.getString("order_status")));
    order.setUpdateDate(resultSet.getTimestamp("update_date"));

    // OrderItem
//    order.setName(resultSet.getString("name"));
//    order.setImageUrl(resultSet.getString("image_url"));
//    order.setDescription(resultSet.getString("description"));
//    order.setQuantity(resultSet.getInt("quantity"));
//    order.setUnitPrice(resultSet.getBigDecimal("unit_price"));
//    order.setPurchaseLocation(resultSet.getString("purchase_location"));

    return order;
  }
}
