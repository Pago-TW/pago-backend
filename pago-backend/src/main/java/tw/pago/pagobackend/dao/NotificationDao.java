package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateNotificationRequestDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.model.NotificationUserMapping;

public interface NotificationDao {

  void createNotification(CreateNotificationRequestDto createNotificationRequestDto);

  Notification getNotificationById(String notificationId);

  void updateNotification(UpdateNotificationRequestDto updateNotificationRequestDto);

  List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto);

  Integer countNotification(ListQueryParametersDto listQueryParametersDto);





}
