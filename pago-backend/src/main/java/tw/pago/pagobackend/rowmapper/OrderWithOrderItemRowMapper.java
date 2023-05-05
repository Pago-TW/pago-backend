package tw.pago.pagobackend.rowmapper;

import com.neovisionaries.i18n.CountryCode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;

public class OrderWithOrderItemRowMapper implements RowMapper<Order> {

  @Override
  public Order mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    OrderItem orderItem = OrderItem.builder()
        .orderItemId(resultSet.getString("order_item_id"))
        .name(resultSet.getString("name"))
        .description(resultSet.getString("description"))
        .quantity(resultSet.getInt("quantity"))
        .unitPrice(resultSet.getBigDecimal("unit_price"))
        .purchaseCountry(CountryCode.valueOf(resultSet.getString("purchase_country")))
        .purchaseCity(CityCode.valueOf(resultSet.getString("purchase_city")))
        .purchaseRoad(resultSet.getString("purchase_road"))
        .build();

    Order order = Order.builder()
        .orderId(resultSet.getString("order_id"))
        .orderItemId(resultSet.getString("order_item_id"))
        .serialNumber(resultSet.getString("serial_number"))
        .consumerId(resultSet.getString("consumer_id"))
        .createDate(resultSet.getTimestamp("create_date"))
        .isPackagingRequired(resultSet.getBoolean("packaging"))
        .isVerificationRequired(resultSet.getBoolean("verification"))
        .destinationCountry(CountryCode.valueOf(resultSet.getString("destination_country")))
        .destinationCity(CityCode.valueOf(resultSet.getString("destination_city")))
        .travelerFee(resultSet.getBigDecimal("traveler_fee"))
        .currency(CurrencyEnum.valueOf(resultSet.getString("currency")))
        .platformFeePercent(resultSet.getDouble("platform_fee_percent"))
        .tariffFeePercent(resultSet.getDouble("tariff_fee_percent"))
        .latestReceiveItemDate(resultSet.getTimestamp("latest_receive_item_date"))
        .note(resultSet.getString("note"))
        .orderStatus(OrderStatusEnum.valueOf(resultSet.getString("order_status")))
        .updateDate(resultSet.getTimestamp("update_date"))
        .orderItem(orderItem)
        .build();

    return order;

  }
}
