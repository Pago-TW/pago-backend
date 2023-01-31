package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.MatchingDao;
import tw.pago.pagobackend.dto.ChooseTravelerDto;


@Component
public class MatchingDaoImpl implements MatchingDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createMatching(ChooseTravelerDto chooseTravelerDto) {
    String sql = "INSERT INTO matching (matching_id, bid_id, create_date, matching_status) "
        + "VALUES (:matchingId, :bidId, :createDate, :matchingStatus)";

    Map<String, Object> map = new HashMap<>();
    map.put("matchingId", chooseTravelerDto.getMatchingId());
    map.put("bidId", chooseTravelerDto.getBidId());

    Date now = new Date();
    map.put("createDate", now);
    map.put("matchingStatus", chooseTravelerDto.getMatchingStatus().toString());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }
}
