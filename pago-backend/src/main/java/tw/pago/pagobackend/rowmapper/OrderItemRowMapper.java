package tw.pago.pagobackend.rowmapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.OrderItem;

public class OrderItemRowMapper implements RowMapper<OrderItem> {

  @Override
  public OrderItem mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    OrderItem orderItem = OrderItem.builder()
        .orderItemId(resultSet.getString("order_item_id"))
        .name(resultSet.getString("name"))
        .description(resultSet.getString("description"))
        .quantity(resultSet.getInt("quantity"))
        .unitPrice(resultSet.getBigDecimal("unit_price"))
        .purchaseCountry(resultSet.getString("purchase_country"))
        .purchaseCity(resultSet.getString("purchase_city"))
        .purchaseDistrict(resultSet.getString("purchase_district"))
        .purchaseRoad(resultSet.getString("purchase_road"))
        .build();

    return orderItem;

  }
}
