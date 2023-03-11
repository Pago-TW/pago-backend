package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.OrderItem;

public class OrderItemRowMapper implements RowMapper<OrderItem> {

  @Override
  public OrderItem mapRow(ResultSet resultSet, int rowNum) throws SQLException {

    OrderItem orderItem = new OrderItem();
    orderItem.setOrderItemId(resultSet.getString("order_item_id"));
    orderItem.setName(resultSet.getString("name"));
    orderItem.setImageUrl(resultSet.getString("image_url"));
    orderItem.setDescription(resultSet.getString("description"));
    orderItem.setQuantity(resultSet.getInt("quantity"));
    orderItem.setUnitPrice(resultSet.getBigDecimal("unit_price"));
    orderItem.setPurchaseCountry(resultSet.getString("purchase_country"));
    orderItem.setPurchaseCity(resultSet.getString("purchase_city"));
    orderItem.setPurchaseDistrict(resultSet.getString("purchase_district"));
    orderItem.setPurchaseRoad(resultSet.getString("purchase_road"));

    return orderItem;
  }
}
