package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.rowmapper.OrderItemRowMapper;
import tw.pago.pagobackend.rowmapper.OrderRowMapper;

@Component
public class OrderDaoImpl implements OrderDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto) {
    String sql =
        "INSERT INTO order_main (order_id, order_item_id, shopper_id, create_date, update_date, packaging, "
            + "verification, destination, traveler_fee, currency, platform_fee_percent, "
            + "tariff_fee_percent, latest_receive_item_date, note, order_status) "
            + "VALUES (:orderId, :orderItemId, :shopperId, :createDate, :updateDate, :packaging, :verification, "
            + ":destination, :travelerFee, :currency, :platformFeePercent, :tariffFeePercent, "
            + ":latestReceiveItemDate, :note, :orderStatus)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", createOrderRequestDto.getOrderId());
    map.put("orderItemId", createOrderRequestDto.getCreateOrderItemDto().getOrderItemId());
    map.put("shopperId", userId);
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("packaging", createOrderRequestDto.getPackaging().toString());
    map.put("verification", createOrderRequestDto.getVerification());
    map.put("destination", createOrderRequestDto.getDestination());
    map.put("travelerFee", createOrderRequestDto.getTravelerFee());
    map.put("currency", createOrderRequestDto.getCurrency().toString());
    map.put("platformFeePercent", createOrderRequestDto.getPlatformFeePercent());
    map.put("tariffFeePercent", createOrderRequestDto.getTariffFeePercent());
    map.put("latestReceiveItemDate", createOrderRequestDto.getLatestReceiveItemDate());
    map.put("note", createOrderRequestDto.getNote());
    map.put("orderStatus", createOrderRequestDto.getOrderStatus().toString());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public void createOrderItem(CreateOrderRequestDto createOrderRequestDto) {
    String sql = "INSERT INTO order_item (order_item_id ,`name`, image_url, description, quantity, unit_price, "
        + "purchase_location) "
        + "VALUES (:orderItemId ,:name, :imageUrl, :description, :quantity, :unitPrice, :purchaseLocation)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderItemId", createOrderRequestDto.getCreateOrderItemDto().getOrderItemId());
    map.put("name", createOrderRequestDto.getCreateOrderItemDto().getName());
    map.put("imageUrl", createOrderRequestDto.getCreateOrderItemDto().getImageUrl());
    map.put("description", createOrderRequestDto.getCreateOrderItemDto().getDescription());
    map.put("quantity", createOrderRequestDto.getCreateOrderItemDto().getQuantity());
    map.put("unitPrice", createOrderRequestDto.getCreateOrderItemDto().getUnitPrice());
    map.put("purchaseLocation",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseLocation());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }


  @Override
  public Order getOrderById(String orderId) {
    String sql =
        "SELECT om.order_id, om.order_item_id, om.shopper_id, om.create_date, om.update_date, om.packaging, "
            + "om.verification, om.destination, om.traveler_fee, om.currency, om.platform_fee_percent, "
            + "om.tariff_fee_percent, om.latest_receive_item_date, om.note, om.order_status , "
            + "oi.name, oi.image_url, oi.description, oi.quantity, oi.unit_price, oi.purchase_location "
            + "FROM order_main as om "
            + "LEFT JOIN order_item as oi ON om.order_item_id = oi.order_item_id "
            + "WHERE om.order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);

    List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

    if (orderList.size() > 0) {
      return orderList.get(0);
    } else {
      return null;
    }
  }


  @Override
  public OrderItem getOrderItemById(String orderItemId) {
    String sql = "SELECT order_item_id, `name`, image_url, description, "
        + "quantity, unit_price, purchase_location "
        + "FROM order_item "
        + "WHERE order_item_id = :orderItemId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderItemId", orderItemId);

    List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map,
        new OrderItemRowMapper());

    if (orderItemList.size() > 0) {
      return orderItemList.get(0);
    } else {
      return null;
    }
  }


  @Override
  public void updateOrderById(UpdateOrderRequestDto updateOrderRequestDto) {
    String sql = "UPDATE order_main "
        + "SET oi.name = :name, oi.image_url = :imageUrl, oi.description = :description, "
        + "oi.quantity = :quantity, oi.unit_price = :unitPrice, oi.purchase_location = :purchaseLocation, "
        + "om.packaging = :packaging, om.verification = :verification, om.destination = :destination, "
        + "om.traveler_fee = :travelerFee, om.currency = :currency, om.latest_receive_item_date = :latestReceiveItemDate, "
        + "om.note = :note, om.update_date = :updateDate "
        + "FROM order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "WHERE om.orderId = :orderId";

    System.out.println("SQL: " + sql);

    Map<String, Object> map = new HashMap<>();
    map.put("name", updateOrderRequestDto.getCreateOrderItemDto().getName());
    map.put("imageUrl", updateOrderRequestDto.getCreateOrderItemDto().getImageUrl());
    map.put("description", updateOrderRequestDto.getCreateOrderItemDto().getDescription());
    map.put("quantity", updateOrderRequestDto.getCreateOrderItemDto().getQuantity());
    map.put("unitPrice", updateOrderRequestDto.getCreateOrderItemDto().getUnitPrice());
    map.put("purchaseLocation", updateOrderRequestDto.getCreateOrderItemDto().getPurchaseLocation());
    map.put("packaging", updateOrderRequestDto.getPackaging());
    map.put("verification", updateOrderRequestDto.getVerification());
    map.put("destination", updateOrderRequestDto.getDestination());
    map.put("travelerFee", updateOrderRequestDto.getTravelerFee());
    map.put("currency", updateOrderRequestDto.getCurrency().toString());
    map.put("latestReceiveItemDate", updateOrderRequestDto.getLatestReceiveItemDate());
    map.put("note", updateOrderRequestDto.getNote());

    Date now = new Date();
    map.put("updateDate", now);
    map.put("orderId", updateOrderRequestDto.getOrderId());
    System.out.println("OrderId = " + updateOrderRequestDto.getOrderId());

    namedParameterJdbcTemplate.update(sql, map);
  }


  @Override
  public void deleteOrderById(Integer orderId) {
    String sql = "DELETE FROM order_main WHERE order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);

    namedParameterJdbcTemplate.update(sql, map);
  }
}
