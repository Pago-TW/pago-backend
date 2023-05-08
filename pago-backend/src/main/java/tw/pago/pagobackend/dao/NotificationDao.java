package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.UpdateNotificationRequestDto;
import tw.pago.pagobackend.model.Notification;

public interface NotificationDao {

  void createNotification(CreateNotificationRequestDto createNotificationRequestDto);

  Notification getNotificationById(String notificationId);

  void updateNotification(UpdateNotificationRequestDto updateNotificationRequestDto);





}
