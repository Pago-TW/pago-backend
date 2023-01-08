package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.rowmapper.OrderItemRowMapper;
import tw.pago.pagobackend.rowmapper.OrderRowMapper;

@Component
public class OrderDaoImpl implements OrderDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public Integer createOrder(Integer userId, CreateOrderRequestDto createOrderRequestDto,
      Integer orderItemId) {
    String sql =
        "INSERT INTO order_main (order_item_id, shopper_id, create_date, update_date, packaging, "
            + "verification, destination, traveler_fee, currency, platform_fee_percent, "
            + "tariff_fee_percent, latest_receive_item_date, note, order_status) "
            + "VALUES (:orderItemId, :shopperId, :createDate, :updateDate, :packaging, :verification, "
            + ":destination, :travelerFee, :currency, :platformFeePercent, :tariffFeePercent, "
            + ":latestReceiveItemDate, :note, :orderStatus)";


    Map<String, Object> map = new HashMap<>();
    map.put("orderItemId", orderItemId);
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

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    int orderId = keyHolder.getKey().intValue();
    return orderId;
  }

  @Override
  public Integer createOrderItem(CreateOrderRequestDto createOrderRequestDto) {
    String sql = "INSERT INTO order_item (`name`, image_url, description, quantity, unit_price, "
        + "purchase_location) "
        + "VALUES (:name, :imageUrl, :description, :quantity, :unitPrice, :purchaseLocation)";

    Map<String, Object> map = new HashMap<>();
    map.put("name", createOrderRequestDto.getCreateOrderItemDto().getName());
    map.put("imageUrl", createOrderRequestDto.getCreateOrderItemDto().getImageUrl());
    map.put("description", createOrderRequestDto.getCreateOrderItemDto().getDescription());
    map.put("quantity", createOrderRequestDto.getCreateOrderItemDto().getQuantity());
    map.put("unitPrice", createOrderRequestDto.getCreateOrderItemDto().getUnitPrice());
    map.put("purchaseLocation",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseLocation());

    KeyHolder keyHolder = new GeneratedKeyHolder();

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    int orderItemId = keyHolder.getKey().intValue();

    return orderItemId;

  }


  @Override
  public Order getOrderById(Integer orderId) {
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
  public OrderItem getOrderItemById(Integer orderItemId) {
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
}
