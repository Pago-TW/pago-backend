package tw.pago.pagobackend.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.rowmapper.BidRowMapper;
import tw.pago.pagobackend.rowmapper.BidWithTripRowMapper;

@Component
public class BidDaoImpl implements BidDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createBid(CreateBidRequestDto createBidRequestDto) {
    String sql = "INSERT INTO bid (bid_id, order_id, trip_id, bid_amount, currency, create_date, update_date,latest_delivery_date, bid_status) "
        + "VALUES (:bidId, :orderId, :tripId, :bidAmount, :currency, :createDate, :updateDate,:latestDeliveryDate, :bidStatus)";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", createBidRequestDto.getBidId());
    map.put("orderId", createBidRequestDto.getOrderId());
    map.put("tripId", createBidRequestDto.getTripId());
    map.put("bidAmount", createBidRequestDto.getBidAmount());
    map.put("currency", createBidRequestDto.getCurrency().toString());
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("latestDeliveryDate", createBidRequestDto.getLatestDeliveryDate());
    map.put("bidStatus", createBidRequestDto.getBidStatus().toString());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public Bid getBidById(String bidId) {
    String sql = "SELECT bid_id, order_id, trip_id, bid_amount, currency, create_date, "
        + "update_date,latest_delivery_date, bid_status "
        + "FROM bid "
        + "WHERE bid_id = :bidId ";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", bidId);


    List<Bid> bidList = namedParameterJdbcTemplate.query(sql, map, new BidRowMapper());

    if (bidList.size() > 0) {
      return bidList.get(0);
    } else {
      return null;
    }
  }


  @Override
  public Bid getBidByOrderIdAndBidId(String orderId, String bidId) {
    String sql = "SELECT bid_id, order_id, trip_id, bid_amount, currency, create_date, "
        + "update_date,latest_delivery_date, bid_status "
        + "FROM bid "
        + "WHERE order_id = :orderId "
        + "AND bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);
    map.put("bidId", bidId);


    List<Bid> bidList = namedParameterJdbcTemplate.query(sql, map, new BidRowMapper());

    if (bidList.size() > 0) {
      return bidList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void deleteBidById(String bidId) {
    String sql = "DELETE FROM bid "
        + "WHERE bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", bidId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void updateBid(UpdateBidRequestDto updateBidRequestDto) {
    String sql = "UPDATE bid SET trip_id = :tripId, bid_amount = :bidAmount, "
    + "currency = :currency, update_date = :updateDate, bid_status = :bidStatus "
    + "WHERE bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("tripId", updateBidRequestDto.getTripId());
    map.put("bidAmount", updateBidRequestDto.getBidAmount());
    map.put("currency", updateBidRequestDto.getCurrency().name());
    Date now = new Date();
    map.put("updateDate", now);
    map.put("bidStatus", updateBidRequestDto.getBidStatus().name());
    map.put("bidId", updateBidRequestDto.getBidId());

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void chooseBid(Bid bid) {
    String sql = "UPDATE bid SET bid_status = :bidStatus , update_date = :updateDate "
        + "WHERE bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("bidStatus", bid.getBidStatus().toString());
    map.put("bidId", bid.getBidId());
    Date now = new Date();
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT trip.shopper_id, "
        + "bid_id, order_id, bid.trip_id, bid_amount, currency, bid.create_date, "
        + "bid.update_date, latest_delivery_date, bid_status "
        + "FROM bid  "
        + "LEFT JOIN trip  "
        + "ON bid.trip_id = trip.trip_id "
        + "WHERE 1=1";

    Map<String, Object> map = new HashMap<>();


    // Filtering e.g. status, search, orderId
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());





    List<Bid> bidList = namedParameterJdbcTemplate.query(sql, map, new BidWithTripRowMapper());

    return bidList;
  }


  @Override
  public Integer countBid(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(bid_id) "
        + "FROM bid "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);


    return total;
  }


  @Override
  public Integer countBidByTripId(String tripId) {
    String sql = "SELECT COUNT(bid_id) "
        + "FROM bid "
        + "WHERE trip_id = :tripId ";

    Map<String, Object> map = new HashMap<>();
    map.put("tripId", tripId);


    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);


    return total;
  }

  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {
    if (listQueryParametersDto.getSearch() != null) {
      sql = sql + "AND ( "
          + "   from_country LIKE :search "
          + "OR from_city LIKE :search "
          + "OR to_country LIKE :search "
          + "OR to_city LIKE :search"
          + ") ";
      map.put("search", "%" + listQueryParametersDto.getSearch() + "%");
    }

    if (listQueryParametersDto.getOrderId() != null) {
      sql = sql + " AND order_id = :orderId ";
      map.put("orderId", listQueryParametersDto.getOrderId());
    }

    if (listQueryParametersDto.getTripId() != null) {
      sql = sql + " AND trip.trip_id = :tripId ";
      map.put("tripId", listQueryParametersDto.getTripId());
    }

    if (listQueryParametersDto.getBidStatus() != null) {
      sql = sql + " AND bid_status = :bidStatus ";
      map.put("bidStatus", listQueryParametersDto.getBidStatus().name());
    }

    return sql;
  }
}
