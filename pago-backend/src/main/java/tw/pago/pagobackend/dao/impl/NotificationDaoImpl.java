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
import tw.pago.pagobackend.dto.CreateNotificationUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateNotificationRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.rowmapper.ChatroomRowMapper;
import tw.pago.pagobackend.rowmapper.NotificationRowMapper;

@Repository
@AllArgsConstructor
public class NotificationDaoImpl implements NotificationDao {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createNotification(CreateNotificationRequestDto createNotificationRequestDto) {
    String sql = "INSERT INTO notification (notification_id, content, create_date, update_date, notification_type, image_url, redirect_url, action_type) "
        + "VALUES (:notificationId, :content, :createDate, :updateDate, :notificationType, :imageUrl, :redirectUrl, :actionType)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();

    map.put("notificationId", createNotificationRequestDto.getNotificationId());
    map.put("content", createNotificationRequestDto.getContent());
    map.put("createDate", now);
    map.put("updateDate", now);
    map.put("notificationType", createNotificationRequestDto.getNotificationType().name());
    map.put("imageUrl", createNotificationRequestDto.getImageUrl());
    map.put("redirectUrl", createNotificationRequestDto.getRedirectUrl());
    map.put("actionType", createNotificationRequestDto.getActionType().name());

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public void createNotificationUserMapping(
      CreateNotificationUserMappingRequestDto createNotificationUserMappingRequestDto) {

    String sql = "INSERT INTO notification_user_mapping (notification_user_mapping_id, notification_id, user_id, is_read, create_date, update_date) "
        + "VALUES (:notificationUserMappingId, :notificationId, :userId, :isRead, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();

    map.put("notificationUserMappingId", createNotificationUserMappingRequestDto.getNotificationUserMappingId());
    map.put("notificationId", createNotificationUserMappingRequestDto.getNotificationId());
    map.put("userId", createNotificationUserMappingRequestDto.getUserId());
    map.put("isRead", createNotificationUserMappingRequestDto.isRead());
    map.put("createDate", now);
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public Notification getNotificationById(String notificationId) {
    String sql = "SELECT notification_id, content, create_date, update_date, notification_type, image_url, redirect_url, action_type "
        + "FROM notification "
        + "WHERE notification_id = :notificationId ";

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

  @Override
  public List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT n.notification_id, n.content, n.create_date, n.update_date, n.notification_type, n.image_url, n.redirect_url, n.action_type "
        + "FROM notification AS n "
        + "JOIN notification_user_mapping AS num ON n.notification_id = num.notification_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. currentLoginUserId, notificationType
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());


    List<Notification> notificationList = namedParameterJdbcTemplate.query(sql, map, new NotificationRowMapper());

    return notificationList;
  }

  @Override
  public Integer countNotification(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(n.notification_id) "
        + "FROM notification AS n "
        + "JOIN notification_user_mapping AS num ON n.notification_id = num.notification_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. currentLoginUserId, notificationType
    sql = addFilteringSql(sql, map, listQueryParametersDto);


    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {

    if (listQueryParametersDto.getUserId() != null) {
      sql = sql + " AND num.user_id = :userId ";
      map.put("userId", listQueryParametersDto.getUserId());
    }

    if (listQueryParametersDto.getNotificationType() != null) {
      sql = sql + " AND n.notification_type = :notificationType ";
      map.put("notificationType", listQueryParametersDto.getNotificationType().name());
    }



    return sql;
  }
}
