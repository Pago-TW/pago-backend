package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.ActionTypeEnum;
import tw.pago.pagobackend.constant.NotificationTypeEnum;
import tw.pago.pagobackend.model.Notification;

public class NotificationRowMapper implements RowMapper<Notification> {

  @Override
  public Notification mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Notification notification = new Notification();
    notification.setNotificationId(resultSet.getString("notification_id"));
    notification.setContent(resultSet.getString("content"));
    notification.setCreateDate(resultSet.getTimestamp("create_date").toInstant().atZone(ZoneId.of("UTC")));
    notification.setUpdateDate(resultSet.getTimestamp("update_date").toInstant().atZone(ZoneId.of("UTC")));
    notification.setNotificationType(
        NotificationTypeEnum.valueOf(resultSet.getString("notification_type")));
    notification.setImageUrl(resultSet.getString("image_url"));
    notification.setRedirectUrl(resultSet.getString("redirect_url"));
    notification.setActionType(ActionTypeEnum.valueOf(resultSet.getString("action_type")));


    return notification;
  }
}
