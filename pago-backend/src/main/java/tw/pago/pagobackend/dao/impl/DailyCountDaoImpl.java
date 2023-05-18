package tw.pago.pagobackend.dao.impl;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.DailyCountDao;
import tw.pago.pagobackend.dto.CreateDailyCountRequestDto;
import tw.pago.pagobackend.model.DailyCount;
import tw.pago.pagobackend.rowmapper.DailyCountRowMapper;

@Repository
@AllArgsConstructor
public class DailyCountDaoImpl implements DailyCountDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createDailyCount(CreateDailyCountRequestDto createDailyCountRequestDto) {
    String sql = "INSERT INTO daily_count (daily_count_id, user_id, sms_count, email_count, create_date, update_date) "
        + "VALUES (:dailyCountId, :userId, :smsCount, :emailCount, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    ZonedDateTime dateAtStartOfDay = now.toLocalDate().atStartOfDay(ZoneId.of("UTC"));

    map.put("dailyCountId", createDailyCountRequestDto.getDailyCountId());
    map.put("userId", createDailyCountRequestDto.getUserId());
    map.put("smsCount", createDailyCountRequestDto.getSmsCount());
    map.put("emailCount", createDailyCountRequestDto.getEmailCount());
    map.put("createDate", Timestamp.from(dateAtStartOfDay.toInstant()));
    map.put("updateDate", Timestamp.from(now.toInstant()));

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }

  @Override
  public DailyCount getDailyCountByUserId(String userId) {
    String sql = "SELECT daily_count_id, user_id, sms_count, email_count, create_date, update_date "
        + "FROM daily_count "
        + "WHERE user_id = :userId ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);

    List<DailyCount> dailyCountList = namedParameterJdbcTemplate.query(sql, map, new DailyCountRowMapper());

    if (dailyCountList.size() > 0) {
      return dailyCountList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public DailyCount getDailyCountByUserIdAndCreateDate(String userId, ZonedDateTime createDate) {
    String sql = "SELECT daily_count_id, user_id, sms_count, email_count, create_date, update_date "
        + "FROM daily_count "
        + "WHERE user_id = :userId AND DATE(create_date) = :createDate ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);
    map.put("createDate", createDate);

    List<DailyCount> dailyCountList = namedParameterJdbcTemplate.query(sql, map, new DailyCountRowMapper());

    if (dailyCountList.size() > 0) {
      return dailyCountList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void incrementTodaySmsCount(String userId, ZonedDateTime today) {
    String sql = "UPDATE daily_count "
        + "SET sms_count = sms_count + 1, update_date = :updateDate "
        + "WHERE user_id = :userId AND DATE(create_date) = :today ";

    Map<String, Object> map = new HashMap<>();
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    map.put("updateDate", Timestamp.from(now.toInstant()));
    map.put("userId", userId);
    map.put("today", today);

    namedParameterJdbcTemplate.update(sql, map);

  }

  @Override
  public void incrementTodayEmailCount(String userId, ZonedDateTime today) {
    String sql = "UPDATE daily_count "
        + "SET email_count = email_count + 1, update_date = :updateDate "
        + "WHERE user_id = :userId AND DATE(create_date) = :today ";

    Map<String, Object> map = new HashMap<>();
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    map.put("updateDate", Timestamp.from(now.toInstant()));
    map.put("userId", userId);
    map.put("today", today);

    namedParameterJdbcTemplate.update(sql, map);
  }
}
