package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.MatchingDao;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.dto.UpdateMatchingRequestDto;
import tw.pago.pagobackend.model.Matching;
import tw.pago.pagobackend.rowmapper.MatchingRowMapper;


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

  @Override
  public Matching getMatching(String matchingId) {
    String sql = "SELECT matching_id, bid_id, create_date, matching_status "
        + "FROM matching "
        + "WHERE matching_id = :matchingId";

    Map<String, Object> map = new HashMap<>();
    map.put("matchingId", matchingId);

    List<Matching> matchingList = namedParameterJdbcTemplate.query(sql, map, new MatchingRowMapper());

    if (matchingList.size() > 0) {
      return matchingList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void updateMatching(UpdateMatchingRequestDto updateMatchingRequestDto) {
    String sql = "UPDATE matching "
        + "SET matching_status = :matchingStatus, update_date = :updateDate "
        + "WHERE matching_id = :matchingId";

    Map<String, Object> map = new HashMap<>();
    map.put("matchingStatus", updateMatchingRequestDto.getMatchingStatus().toString());

    Date now = new Date();
    map.put("updateDate", now);
    map.put("matchingId", updateMatchingRequestDto.getMatchingId());

    namedParameterJdbcTemplate.update(sql, map);
  }
}
