package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.DailyCount;

public class DailyCountRowMapper implements RowMapper<DailyCount> {

  @Override
  public DailyCount mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    DailyCount dailyCount = new DailyCount();
    dailyCount.setDailyCountId(resultSet.getString("daily_count_id"));
    dailyCount.setUserId(resultSet.getString("user_id"));
    dailyCount.setSmsCount(resultSet.getInt("sms_count"));
    dailyCount.setEmailCount(resultSet.getInt("email_count"));
    dailyCount.setCreateDate(resultSet.getTimestamp("create_date").toInstant().atZone(ZoneId.of("UTC")));
    dailyCount.setUpdateDate(resultSet.getTimestamp("update_date").toInstant().atZone(ZoneId.of("UTC")));

    return dailyCount;
  }
}
