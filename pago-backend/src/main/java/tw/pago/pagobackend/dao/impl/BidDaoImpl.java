package tw.pago.pagobackend.dao.impl;

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
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.rowmapper.BidRowMapper;

@Component
public class BidDaoImpl implements BidDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createBid(CreateBidRequestDto createBidRequestDto) {
    String sql = "INSERT INTO bid (bid_id, order_id, trip_id, bid_amount, currency, create_date, update_date, bid_status) "
        + "VALUES (:bidId, :orderId, :tripId, :bidAmount, :currency, :createDate, :updateDate, :bidStatus)";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", createBidRequestDto.getBidId());
    map.put("orderId", createBidRequestDto.getOrderId());
    map.put("tripId", createBidRequestDto.getTripId());
    map.put("bidAmount", createBidRequestDto.getBidAmount());
    map.put("currency", createBidRequestDto.getCurrency().toString());
    Date now = new Date();
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("bidStatus", createBidRequestDto.getBidStatus().toString());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public Bid getBidById(String bidId) {
    String sql = "SELECT bid_id, order_id, trip_id, bid_amount, currency, create_date, update_date, bid_status "
        + "FROM bid "
        + "WHERE bid_id = :bidId";

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
  public void deleteBidById(String bidId) {
    String sql = "DELETE FROM bid "
        + "WHERE bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", bidId);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public void updateBid(Bid bid, UpdateBidRequestDto updateBidRequestDto) {
    String sql = "UPDATE bid SET order_id = :orderId, trip_id = :tripId, bid_amount = :bidAmount, "
    + "currency = :currency, update_date = :updateDate, bid_status = :bidStatus "
    + "WHERE bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", updateBidRequestDto.getOrderId() != null ? updateBidRequestDto.getOrderId() : bid.getOrderId());
    map.put("tripId", updateBidRequestDto.getTripId() != null ? updateBidRequestDto.getTripId() : bid.getTripId());
    map.put("bidAmount", updateBidRequestDto.getBidAmount() != null ? updateBidRequestDto.getBidAmount() : bid.getBidAmount());
    map.put("currency", updateBidRequestDto.getCurrency() != null ? updateBidRequestDto.getCurrency().toString() : bid.getCurrency().toString());
    Date now = new Date();
    map.put("updateDate", now);
    map.put("bidStatus", updateBidRequestDto.getBidStatus() != null ? updateBidRequestDto.getBidStatus().toString() : bid.getBidStatus().toString());
    map.put("bidId", updateBidRequestDto.getBidId());

    System.out.println(sql);
    namedParameterJdbcTemplate.update(sql, map);
  }
}
