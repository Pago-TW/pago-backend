package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dto.CreateFavoriteOrderRequestDto;
import tw.pago.pagobackend.dto.CreateOrderRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.OrderItem;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.rowmapper.OrderItemRowMapper;
import tw.pago.pagobackend.rowmapper.OrderWithOrderItemRowMapper;
import tw.pago.pagobackend.rowmapper.ShopperIdRowMapper;

@Component
public class OrderDaoImpl implements OrderDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createOrder(String userId, CreateOrderRequestDto createOrderRequestDto) {
    String sql =
        "INSERT INTO order_main (order_id, order_item_id, serial_number, consumer_id, create_date, update_date, packaging, "
            + "verification, destination_country, destination_city, traveler_fee, currency, platform_fee_percent, "
            + "tariff_fee_percent, latest_receive_item_date, note, order_status) "
            + "VALUES (:orderId, :orderItemId, :serialNumber, :consumerId, :createDate, :updateDate, :packaging, :verification, "
            + ":destinationCountry, :destinationCity, :travelerFee, :currency, :platformFeePercent, :tariffFeePercent, "
            + ":latestReceiveItemDate, :note, :orderStatus)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", createOrderRequestDto.getOrderId());
    map.put("orderItemId", createOrderRequestDto.getCreateOrderItemDto().getOrderItemId());
    map.put("serialNumber", createOrderRequestDto.getSerialNumber());
    map.put("consumerId", userId);
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("packaging", createOrderRequestDto.getIsPackagingRequired());
    map.put("verification", createOrderRequestDto.getIsVerificationRequired());
    map.put("destinationCountry", createOrderRequestDto.getDestinationCountry().name());
    map.put("destinationCity", createOrderRequestDto.getDestinationCity().name());
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
        + "purchase_country, purchase_city, purchase_road) "
        + "VALUES (:orderItemId ,:name, :description, :quantity, :unitPrice, :purchaseCountry, :purchaseCity,"
        + " :purchaseRoad)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderItemId", createOrderRequestDto.getCreateOrderItemDto().getOrderItemId());
    map.put("name", createOrderRequestDto.getCreateOrderItemDto().getName());
    map.put("description", createOrderRequestDto.getCreateOrderItemDto().getDescription());
    map.put("quantity", createOrderRequestDto.getCreateOrderItemDto().getQuantity());
    map.put("unitPrice", createOrderRequestDto.getCreateOrderItemDto().getUnitPrice());
    map.put("purchaseCountry",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseCountry().name());
    map.put("purchaseCity",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseCity().name());
    map.put("purchaseRoad",
        createOrderRequestDto.getCreateOrderItemDto().getPurchaseRoad());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }

  @Override
  public void createFavoriteOrder(CreateFavoriteOrderRequestDto createFavoriteOrderRequestDto) {
    String sql = "INSERT INTO user_favorite_order (user_favorite_order_id ,order_id, user_id) "
        + "VALUES (:userFavoriteOrderId ,:orderId, :userId) ";

    Map<String, Object> map = new HashMap<>();
    map.put("userFavoriteOrderId", createFavoriteOrderRequestDto.getUserFavoriteOrderId());
    map.put("orderId", createFavoriteOrderRequestDto.getOrderId());
    map.put("userId", createFavoriteOrderRequestDto.getUserId());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));


  }


  @Override
  public Order getOrderById(String orderId) {
    String sql =
        "SELECT om.order_id, om.order_item_id, om.serial_number ,om.consumer_id, om.create_date, om.update_date, om.packaging, "
            + "om.verification, om.destination_country, om.destination_city, om.traveler_fee, "
            + "om.currency, om.platform_fee_percent, "
            + "om.tariff_fee_percent, om.latest_receive_item_date, om.note, om.order_status , "
            + "oi.name, oi.description, oi.quantity, oi.unit_price, oi.purchase_country, oi.purchase_city, "
            + "oi.purchase_road "
            + "FROM order_main as om "
            + "LEFT JOIN order_item as oi ON om.order_item_id = oi.order_item_id "
            + "WHERE om.order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);

    List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderWithOrderItemRowMapper());

    if (orderList.size() > 0) {
      return orderList.get(0);
    } else {
      return null;
    }
  }


  @Override
  public Order getOrderByUserIdAndOrderId(String userId, String orderId) {
    String sql =
        "SELECT om.order_id, om.order_item_id, om.serial_number, om.consumer_id, om.create_date, om.update_date, om.packaging, "
            + "om.verification, om.destination_country, om.destination_city, om.traveler_fee, "
            + "om.currency, om.platform_fee_percent, "
            + "om.tariff_fee_percent, om.latest_receive_item_date, om.note, om.order_status , "
            + "oi.name, oi.description, oi.quantity, oi.unit_price, oi.purchase_country, oi.purchase_city, "
            + "oi.purchase_road "
            + "FROM order_main AS om "
            + "LEFT JOIN order_item AS oi ON om.order_item_id = oi.order_item_id "
            + "WHERE om.consumer_id = :userId "
            + "AND om.order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);
    map.put("orderId", orderId);

    List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderWithOrderItemRowMapper());

    if (orderList.size() > 0) {
      return orderList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public String getChosenBidderIdByOrderId(String orderId) {
    String sql = "SELECT t.shopper_id "
        + "FROM trip AS t "
        + "LEFT JOIN bid AS b ON b.trip_id = t.trip_id "
        + "LEFT JOIN order_main AS om ON om.order_id = b.order_id "
        + "WHERE b.bid_status = :bidStatus "
        + "AND om.order_id = :orderId ";

    System.out.println("SQL: " + sql);

    Map<String, Object> map = new HashMap<>();
    map.put("bidStatus", BidStatusEnum.IS_CHOSEN.name());
    map.put("orderId", orderId);

    List<String> shopperIdList = namedParameterJdbcTemplate.query(sql, map, new ShopperIdRowMapper());

    if (shopperIdList.size() > 0) {
      return shopperIdList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public OrderItem getOrderItemById(String orderItemId) {
    String sql = "SELECT order_item_id, `name`, description, "
        + "quantity, unit_price, purchase_country, purchase_city, purchase_road "
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
  public void updateOrderAndOrderItemByOrderId(UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto) {
    String sql = "UPDATE order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "SET oi.name = :name, oi.description = :description, "
        + "oi.quantity = :quantity, oi.unit_price = :unitPrice, "
        + "oi.purchase_country = :purchaseCountry, oi.purchase_city = :purchaseCity, "
        + "oi.purchase_road = :purchaseRoad, "
        + "om.packaging = :packaging, om.verification = :verification, "
        + "om.destination_country = :destinationCountry, om.destination_city = :destinationCity, "
        + "om.traveler_fee = :travelerFee, om.currency = :currency, "
        + "om.latest_receive_item_date = :latestReceiveItemDate, "
        + "om.note = :note, om.order_status = :orderStatus, om.update_date = :updateDate "
        + "WHERE om.order_Id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("name", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getName());
    map.put("description", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getDescription());
    map.put("quantity", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getQuantity());
    map.put("unitPrice", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getUnitPrice());
    map.put("purchaseCountry", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getPurchaseCountry().name());
    map.put("purchaseCity", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getPurchaseCity().name());
    map.put("purchaseRoad", updateOrderAndOrderItemRequestDto.getUpdateOrderItemDto().getPurchaseRoad());
    map.put("packaging", updateOrderAndOrderItemRequestDto.getIsPackagingRequired());
    map.put("verification", updateOrderAndOrderItemRequestDto.getIsVerificationRequired());
    map.put("destinationCountry", updateOrderAndOrderItemRequestDto.getDestinationCountry().name());
    map.put("destinationCity", updateOrderAndOrderItemRequestDto.getDestinationCity().name());
    map.put("travelerFee", updateOrderAndOrderItemRequestDto.getTravelerFee());
    map.put("currency", updateOrderAndOrderItemRequestDto.getCurrency().name());
    map.put("latestReceiveItemDate", updateOrderAndOrderItemRequestDto.getLatestReceiveItemDate());
    map.put("note", updateOrderAndOrderItemRequestDto.getNote());
    map.put("orderStatus", updateOrderAndOrderItemRequestDto.getOrderStatus().name());
    Date now = new Date();
    map.put("updateDate", now);
    map.put("orderId", updateOrderAndOrderItemRequestDto.getOrderId());

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void updateOrderStatusByOrderId(String orderId, OrderStatusEnum updatedOrderStatus) {
    String sql = "UPDATE order_main "
        + "SET order_status = :orderStatus, update_date = :updateDate "
        + "WHERE order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderStatus", updatedOrderStatus.name());
    Date now = new Date();
    map.put("updateDate", now);
    map.put("orderId", orderId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void deleteOrderById(String orderId) {
    String sql = "DELETE FROM order_main WHERE order_id = :orderId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void deleteOrderItemById(String orderItemId) {
    String sql = "DELETE FROM order_item WHERE order_item_id = :orderItemId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderItemId", orderItemId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public List<Order> getOrderList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT om.order_id, om.order_item_id, om.serial_number,om.consumer_id, om.create_date, om.update_date, om.packaging, "
        + "om.verification, om.destination_country, om.destination_city, om.traveler_fee, om.currency, om.platform_fee_percent, "
        + "om.tariff_fee_percent, om.latest_receive_item_date, om.note, om.order_status , "
        + "oi.name, oi.description, oi.quantity, oi.unit_price, oi.purchase_country, oi.purchase_city, "
        + "oi.purchase_road "
        + "FROM order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search, userId
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());

    List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderWithOrderItemRowMapper());


    return orderList;

  }


  @Override
  public List<Order> getMatchingOrderListForTrip(ListQueryParametersDto listQueryParametersDto,
      Trip trip) {
    String sql = "SELECT om.order_id, om.order_item_id, om.serial_number, om.consumer_id, om.create_date, om.update_date, om.packaging, "
        + "om.verification, om.destination_country, om.destination_city, om.traveler_fee, om.currency, om.platform_fee_percent, "
        + "om.tariff_fee_percent, om.latest_receive_item_date, om.note, om.order_status , "
        + "oi.name, oi.description, oi.quantity, oi.unit_price, oi.purchase_country, oi.purchase_city, "
        + "oi.purchase_road "
        + "FROM order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search, userId
    sql = addFilteringSql(sql, map, listQueryParametersDto);


    // TripConditions e.g. toCountry, toCity, arrivalDate
    sql = addTripConditionSql(sql, map, trip);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());

    List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderWithOrderItemRowMapper());


    return orderList;

  }

  @Override
  public Integer countOrder(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(order_id) "
        + "FROM order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  @Override
  public Integer countMatchingOrderForTrip(ListQueryParametersDto listQueryParametersDto,
      Trip trip) {
    String sql = "SELECT COUNT(order_id) "
        + "FROM order_main AS om "
        + "LEFT JOIN order_item AS oi "
        + "ON om.order_item_id = oi.order_item_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // TripConditions e.g. toCountry, toCity, arrivalDate
    sql = addTripConditionSql(sql, map, trip);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
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

  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {
    if (listQueryParametersDto.getUserId() != null) {
      sql = sql + " AND om.consumer_id = :consumerId ";
      map.put("consumerId", listQueryParametersDto.getUserId());
    }

    if (listQueryParametersDto.getTripId() != null) {
      sql = sql + " AND om.order_id IN (SELECT b.order_id FROM bid AS b WHERE b.trip_id = :tripId) ";
      map.put("tripId", listQueryParametersDto.getTripId());
    }


    if (listQueryParametersDto.getOrderStatus() != null) {
      sql = sql + " AND order_status = :orderStatus ";
      map.put("orderStatus", listQueryParametersDto.getOrderStatus().name());
    }

    if (listQueryParametersDto.getSearch() != null) {
      sql = sql + " AND oi.name LIKE :search ";
      map.put("search", "%" + listQueryParametersDto.getSearch() + "%");
    }

    if (listQueryParametersDto.getFromCountry() != null) {
      if (listQueryParametersDto.getFromCountry().equals(CountryCode.ANY)) {
        sql = sql + " AND oi.purchase_country IS NOT NULL ";
      } else {
        sql = sql + " AND oi.purchase_country = :purchaseCountry ";
        map.put("purchaseCountry", listQueryParametersDto.getFromCountry().name());
      }
    }

    if (listQueryParametersDto.getFromCity() != null) {
      if (listQueryParametersDto.getFromCity().equals(CityCode.ANY)) {
        sql = sql + " AND oi.purchase_country IS NOT NULL ";
      } else {
        sql = sql + " AND oi.purchase_city = :purchaseCity ";
        map.put("purchaseCity", listQueryParametersDto.getFromCity().name());
      }
    }

    if (listQueryParametersDto.getToCountry() != null) {
      if (listQueryParametersDto.getToCountry().equals(CountryCode.ANY)) {
        sql = sql + " AND om.destination_country IS NOT NULL ";
      } else {
        sql = sql + " AND om.destination_country = :destinationCountry ";
        map.put("destinationCountry", listQueryParametersDto.getToCountry().name());
      }
    }

    if (listQueryParametersDto.getToCity() != null) {
      if (listQueryParametersDto.getToCity().equals(CityCode.ANY)) {
        sql = sql + " AND om.destination_city IS NOT NULL ";
      } else {
        sql = sql + " AND om.destination_city = :destinationCity ";
        map.put("destinationCity", listQueryParametersDto.getToCity().name());
      }
    }

    if (listQueryParametersDto.getIsPackagingRequired() != null) {
      sql = sql + " AND om.packaging = :packaging ";
      map.put("packaging", listQueryParametersDto.getIsPackagingRequired());
    }


    return sql;
  }

  private String addTripConditionSql(String sql, Map<String, Object> map, Trip trip) {
    sql = sql + " AND (oi.purchase_country = :fromCountry OR oi.purchase_country = 'ANY') ";
    map.put("fromCountry", trip.getFromCountry().name());

    sql = sql + " AND (oi.purchase_city = :fromCity OR oi.purchase_city = 'ANY') ";
    map.put("fromCity", trip.getFromCity().name());

    sql = sql + " AND om.destination_country = :toCountry ";
    map.put("toCountry", trip.getToCountry().name());

    sql = sql + " AND om.destination_city = :toCity  ";
    map.put("toCity", trip.getToCity().name());


    sql = sql + " AND om.latest_receive_item_date >= :arrivalDate ";
    map.put("arrivalDate", trip.getArrivalDate());

    return sql;
  }

}
