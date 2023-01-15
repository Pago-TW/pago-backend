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
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.rowmapper.BidRowMapper;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class BidDaoImpl implements BidDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  @Autowired
  private UuidGenerator uuidGenerator;


  @Override
  public void createBid(CreateBidRequestDto createBidRequestDto) {
    String sql = "INSERT INTO bid (bid_id, order_id, trip_id, bid_amount, currency, create_date) "
        + "VALUES (:bidId, :orderId, :tripId, :bidAmount, :currency, :createDate)";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", createBidRequestDto.getBidId());
    map.put("orderId", createBidRequestDto.getOrderId());
    map.put("tripId", createBidRequestDto.getTripId());
    map.put("bidAmount", createBidRequestDto.getBidAmount());
    map.put("currency", createBidRequestDto.getCurrency().toString());
    Date now = new Date();
    map.put("createDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public Bid getBidById(String bidId) {
    String sql = "SELECT bid_id, order_id, trip_id, bid_amount, currency, create_date "
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
  public void deleteBidById(Integer bidId) {
    String sql = "DELETE FROM bid "
        + "WHERE bid_id = :bidId";

    Map<String, Object> map = new HashMap<>();
    map.put("bidId", bidId);

    namedParameterJdbcTemplate.update(sql, map);
  }
}
