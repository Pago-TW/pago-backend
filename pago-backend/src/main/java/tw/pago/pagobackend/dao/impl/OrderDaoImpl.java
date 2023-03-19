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
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
// import tw.pago.pagobackend.dto.UpdateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.rowmapper.OrderItemRowMapper;
import tw.pago.pagobackend.rowmapper.OrderRowMapper;

@Component
public class OrderDaoImpl implements OrderDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createOrder(String userId, CreateOrderRequestDto createOrderRequestDto) {
    String sql =
        "INSERT INTO order_main (order_id, order_item_id, consumer_id, create_date, update_date, packaging, "
            + "verification, destination, traveler_fee, currency, platform_fee_percent, "
            + "tariff_fee_percent, latest_receive_item_date, note, order_status) "
            + "VALUES (:orderId, :orderItemId, :consumerId, :createDate, :updateDate, :packaging, :verification, "
            + ":destination, :travelerFee, :currency, :platformFeePercent, :tariffFeePercent, "
            + ":latestReceiveItemDate, :note, :orderStatus)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", createOrderRequestDto.getOrderId());
    map.put("orderItemId", createOrderRequestDto.getCreateOrderItemDto().getOrderItemId());
    map.put("consumerId", userId);
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("packaging", createOrderRequestDto.getPackaging());
    map.put("verification", createOrderRequestDto.getVerification());
    map.put("destination", createOrderRequestDto.getDestination());
    map.put("travelerFee", createOrderRequestDto.getTravelerFee());
    map.put("currency", createOrderRequestDto.getCurrency().toString());
    System.out.println(createOrderRequestDto.getPlatformFeePercent());
    System.out.println(createOrderRequestDto.getTariffFeePercent());
    map.put("platformFeePercent", createOrderRequestDto.getPlatformFeePercent());
    map.put("tariffFeePercent", createOrderRequestDto.getTariffFeePercent());
    map.put("latestReceiveItemDate", createOrderRequestDto.getLatestReceiveItemDate());
    map.put("note", createOrderRequestDto.getNote());
    map.put("orderStatus", createOrderRequestDto.getOrderStatus().toString());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public void createOrderItem(CreateOrderRequestDto createOrderRequestDto) {
    String sql = "INSERT INTO order_item (order_item_id ,`name`, description, quantity, unit_price, "
        + "purchase_country, purchase_city, purchase_district, purchase_road) "
        + "VALUES (:orderItemId ,:name, :description, :quantity, :unitPrice, :purchaseCountry, :purchaseCity,"
        + ":purchaseDistrict, :purchaseRoad)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderItemId", createOrderRequestDto.getCreateOrderItemDto().getOrderItemId());
    map.put("name", createOrderRequestDto.getCreateOrderItemDto().getName());
    map.put("description", createOrderRequestDto.getCreateOrderItemDto().getDescription());
    map.put("quantity", createOrderRequestDto.getCreateOrderItemDto().getQuantity());
    map.put("unitPrice", createOrderRequestDto.getCreateOrderItemDto().getUnitPrice());
    map.put("purchaseCountry",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseCountry());
    map.put("purchaseCity",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseCity());
    map.put("purchaseDistrict",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseDistrict());
    map.put("purchaseRoad",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseRoad());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }


  @Override
  public Order getOrderById(String orderId) {
    String sql =
        "SELECT om.order_id, om.order_item_id, om.consumer_id, om.create_date, om.update_date, om.packaging, "
            + "om.verification, om.destination, om.traveler_fee, om.currency, om.platform_fee_percent, "
            + "om.tariff_fee_percent, om.latest_receive_item_date, om.note, om.order_status , "
            + "oi.name, oi.description, oi.quantity, oi.unit_price, oi.purchase_country, oi.purchase_city,"
            + "oi.purchase_district, oi.purchase_road "
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
    String sql = "SELECT order_item_id, `name`, description, "
        + "quantity, unit_price, purchase_country, purchase_city, purchase_district, purchase_road "
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
  public void updateOrderAndOrderItemByOrderId(Order order, UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {
    String sql = "UPDATE order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "SET oi.name = :name, oi.description = :description, "
        + "oi.quantity = :quantity, oi.unit_price = :unitPrice, "
        + "oi.purchase_country = :purchaseCountry, oi.purchase_city = :purchaseCity, "
        + "oi.purchase_district = :purchaseDistrict, oi.purchase_road = :purchaseRoad, "
        + "om.packaging = :packaging, om.verification = :verification, om.destination = :destination, "
        + "om.traveler_fee = :travelerFee, om.currency = :currency, om.latest_receive_item_date = :latestReceiveItemDate, "
        + "om.note = :note, om.order_status = :orderStatus, om.update_date = :updateDate "
        + "WHERE om.order_Id = :orderId";

    Map<String, Object> map = new HashMap<>();

    map.put("name", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getName() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getName() : order.getOrderItem().getName());
    map.put("description", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getDescription() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getDescription() : order.getOrderItem().getDescription());
    map.put("quantity", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getQuantity() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getQuantity() : order.getOrderItem().getQuantity());
    map.put("unitPrice", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getUnitPrice() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getUnitPrice() : order.getOrderItem().getUnitPrice());
    map.put("purchaseCountry", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseCountry() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseCountry() : order.getOrderItem().getPurchaseCountry());
    map.put("purchaseCity", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseCity() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseCity() : order.getOrderItem().getPurchaseCity());
    map.put("purchaseDistrict", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseDistrict() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseDistrict() : order.getOrderItem().getPurchaseDistrict());
    map.put("purchaseRoad", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto() != null && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().isPresent() && 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseRoad() != null ? 
    updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().get().getPurchaseRoad() : order.getOrderItem().getPurchaseRoad());
    
    Boolean packaging = updateOrderAndOrderItemRequestDto.getPackaging();
    map.put("packaging", packaging != null ? packaging : order.getPackaging());

    Boolean verification = updateOrderAndOrderItemRequestDto.getVerification();
    map.put("verification", verification != null ? verification : order.getVerification());

    map.put("destination", updateOrderAndOrderItemRequestDto.getDestination() != null ? updateOrderAndOrderItemRequestDto.getDestination() : order.getDestination());
    map.put("travelerFee", updateOrderAndOrderItemRequestDto.getTravelerFee() != null ? updateOrderAndOrderItemRequestDto.getTravelerFee() : order.getTravelerFee());
    map.put("currency", updateOrderAndOrderItemRequestDto.getCurrency() != null ? updateOrderAndOrderItemRequestDto.getCurrency().toString() : order.getCurrency().toString());
    map.put("latestReceiveItemDate", updateOrderAndOrderItemRequestDto.getLatestReceiveItemDate() != null ? updateOrderAndOrderItemRequestDto.getLatestReceiveItemDate() : order.getLatestReceiveItemDate());
    map.put("note", updateOrderAndOrderItemRequestDto.getNote() != null ? updateOrderAndOrderItemRequestDto.getNote() : order.getNote());
    map.put("orderStatus", updateOrderAndOrderItemRequestDto.getOrderStatus() != null ? updateOrderAndOrderItemRequestDto.getOrderStatus().toString() : order.getOrderStatus().toString());

    Date now = new Date();
    map.put("updateDate", now);
    map.put("orderId", updateOrderAndOrderItemRequestDto.getOrderId());

    namedParameterJdbcTemplate.update(sql, map);
  }


  @Override
  public void deleteOrderById(String orderId) {
    String sql = "DELETE FROM order_main WHERE order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  // @Override
  // public void updateOrder(UpdateOrderRequestDto updateOrderRequestDto) {
  //   String sql = "UPDATE order_main "
  //       + "SET packaging = :packaging, verification = :verification, destination = :destination, "
  //       + "traveler_fee = :travelerFee, currency = :currency, latest_receive_item_date = :latestReceiveItemDate, "
  //       + "note = :note, update_date = :updateDate, order_status = :orderStatus "
  //       + "WHERE order_Id = :orderId";

  //   Map<String, Object> map = new HashMap<>();
  //   map.put("packaging", updateOrderRequestDto.getPackaging());
  //   map.put("verification", updateOrderRequestDto.getVerification());
  //   map.put("destination", updateOrderRequestDto.getDestination());
  //   map.put("travelerFee", updateOrderRequestDto.getTravelerFee());
  //   map.put("currency", updateOrderRequestDto.getCurrency().toString());
  //   map.put("latestReceiveItemDate", updateOrderRequestDto.getLatestReceiveItemDate());
  //   map.put("note", updateOrderRequestDto.getNote());

  //   Date now = new Date();
  //   map.put("updateDate", now);
  //   map.put("orderStatus", updateOrderRequestDto.getOrderStatus().toString());
  //   map.put("orderId", updateOrderRequestDto.getOrderId());

  //   namedParameterJdbcTemplate.update(sql, map);
  // }
}