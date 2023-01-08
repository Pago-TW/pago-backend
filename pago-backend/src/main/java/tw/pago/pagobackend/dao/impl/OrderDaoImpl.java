package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;

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

    System.out.println("sql2: " + sql);

    Map<String, Object> map = new HashMap<>();
    System.out.println("00");
    map.put("orderItemId", orderItemId);
    System.out.println("01");
    map.put("shopperId", userId);
    System.out.println("02");
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("packaging", createOrderRequestDto.getPackaging().toString());
    System.out.println("03");
    map.put("verification", createOrderRequestDto.getVerification());
    System.out.println("04");
    map.put("destination", createOrderRequestDto.getDestination());
    System.out.println("05");
    map.put("travelerFee", createOrderRequestDto.getTravelerFee());
    System.out.println("06");
    map.put("currency", createOrderRequestDto.getCurrency().toString());
    System.out.println("07");
    map.put("platformFeePercent", createOrderRequestDto.getPlatformFeePercent());
    System.out.println("08");
    map.put("tariffFeePercent", createOrderRequestDto.getTariffFeePercent());
    System.out.println("09");
    map.put("latestReceiveItemDate", createOrderRequestDto.getLatestReceiveItemDate());
    System.out.println("10");
    map.put("note", createOrderRequestDto.getNote());
    System.out.println("11");
    map.put("orderStatus", createOrderRequestDto.getOrderStatus().toString());
    System.out.println("12");

    KeyHolder keyHolder = new GeneratedKeyHolder();
    System.out.println("13");
    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    int orderId = keyHolder.getKey().intValue();
    System.out.println("14");
    return orderId;
  }

  @Override
  public Integer createOrderItem(CreateOrderRequestDto createOrderRequestDto) {
    String sql = "INSERT INTO order_item (`name`, image_url, description, quantity, unit_price, "
        + "purchase_location) "
        + "VALUES (:name, :imageUrl, :description, :quantity, :unitPrice, :purchaseLocation)";

    System.out.println("Item sql: " + sql);
    Map<String, Object> map = new HashMap<>();
    map.put("name", createOrderRequestDto.getCreateOrderItemDto().getName());
    map.put("imageUrl", createOrderRequestDto.getCreateOrderItemDto().getImageUrl());
    map.put("description", createOrderRequestDto.getCreateOrderItemDto().getDescription());
    map.put("quantity", createOrderRequestDto.getCreateOrderItemDto().getQuantity());
    map.put("unitPrice", createOrderRequestDto.getCreateOrderItemDto().getUnitPrice());
    map.put("purchaseLocation", createOrderRequestDto.getCreateOrderItemDto().getPurchaseLocation());

    KeyHolder keyHolder = new GeneratedKeyHolder();

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    int orderItemId = keyHolder.getKey().intValue();

    return orderItemId;

  }
}
