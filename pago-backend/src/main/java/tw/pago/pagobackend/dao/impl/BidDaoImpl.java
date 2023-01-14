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
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.rowmapper.BidRowMapper;

@Component
public class BidDaoImpl implements BidDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Override
  public Integer createBid(CreateBidRequestDto createBidRequestDto) {
    String sql = "INSERT INTO bid (order_id, trip_id, bid_amount, currency, create_date) "
        + "VALUES (:orderId, :tripId, :bidAmount, :currency, :createDate)";

    Map<String, Object> map = new HashMap<>();
    map.put("orderId", createBidRequestDto.getOrderId());
    map.put("tripId", createBidRequestDto.getTripId());
    map.put("bidAmount", createBidRequestDto.getBidAmount());
    map.put("currency", createBidRequestDto.getCurrency().toString());
    Date now = new Date();
    map.put("createDate", now);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    int bidId = keyHolder.getKey().intValue();

    return bidId;
  }

  @Override
  public Bid getBidById(Integer bidId) {
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
}
