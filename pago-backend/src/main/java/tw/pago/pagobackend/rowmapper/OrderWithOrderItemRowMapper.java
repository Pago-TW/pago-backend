package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;

public class OrderWithOrderItemRowMapper implements RowMapper<Order> {

  @Override
  public Order mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Order order = new Order();
    order.setOrderId(resultSet.getString("order_id"));
    order.setOrderItemId(resultSet.getString("order_item_id"));
    order.setConsumerId(resultSet.getString("consumer_id"));
    order.setCreateDate(resultSet.getTimestamp("create_date"));
    order.setPackaging(resultSet.getBoolean("packaging"));
    order.setVerification(resultSet.getBoolean("verification"));
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
    OrderItem orderItem = new OrderItem();
    orderItem.setOrderItemId(resultSet.getString("order_item_id"));
    orderItem.setName(resultSet.getString("name"));
    orderItem.setDescription(resultSet.getString("description"));
    orderItem.setQuantity(resultSet.getInt("quantity"));
    orderItem.setUnitPrice(resultSet.getBigDecimal("unit_price"));
    orderItem.setPurchaseCountry(resultSet.getString("purchase_country"));
    orderItem.setPurchaseCity(resultSet.getString("purchase_city"));
    orderItem.setPurchaseDistrict(resultSet.getString("purchase_district"));
    orderItem.setPurchaseRoad(resultSet.getString("purchase_road"));

    // Set OrderItem to Order
    order.setOrderItem(orderItem);

    return order;
  }
}
