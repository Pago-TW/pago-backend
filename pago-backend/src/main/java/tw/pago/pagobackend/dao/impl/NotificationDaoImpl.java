package tw.pago.pagobackend.dao.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.NotificationDao;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.UpdateNotificationRequestDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.rowmapper.NotificationRowMapper;

@Repository
@AllArgsConstructor
public class NotificationDaoImpl implements NotificationDao {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createNotification(CreateNotificationRequestDto createNotificationRequestDto) {
    String sql = "INSERT INTO notification (notification_id, content, create_date, update_date, notification_type) "
        + "VALUES (:notificationId, :content, :createDate, :updateDate, :notificationType)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();

    map.put("chatroomId", createNotificationRequestDto.getNotificationId());
    map.put("content", createNotificationRequestDto.getContent());
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("notificationType", createNotificationRequestDto.getNotificationType().name());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public Notification getNotificationById(String notificationId) {
    String sql = "SELECT notification_id, content, create_date, update_date, notification_type "
        + "FROM notification "
        + "WHERE notificationm_id = :notificationId ";

    Map<String, Object> map = new HashMap<>();
    map.put("notificationId", notificationId);


    List<Notification> notificationList = namedParameterJdbcTemplate.query(sql, map, new NotificationRowMapper());

    if (notificationList.size() > 0) {
      return notificationList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void updateNotification(UpdateNotificationRequestDto updateNotificationRequestDto) {
    String sql = "UPDATE notification "
        + "SET content = :content, update_date = :updateDate "
        + "WHERE notification_id = :notificationId ";

    Map<String, Object> map = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();
    map.put("content", updateNotificationRequestDto.getContent());
    map.put("updateDate", now);
    map.put("notificationId", updateNotificationRequestDto.getNotificationId());

    namedParameterJdbcTemplate.update(sql, map);

  }
}
