package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.CreateNotificationUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.model.NotificationUserMapping;

public interface NotificationService {

  Notification createNotification(CreateNotificationRequestDto createNotificationRequestDto);

  NotificationUserMapping createNotificationUserMapping(CreateNotificationUserMappingRequestDto createNotificationUserMappingRequestDto);

  void sendNotification(Notification notification, String receiverId);

  Notification getNotificationById(String notificationId);

  List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto);


  Integer countNotification(ListQueryParametersDto listQueryParametersDto);

}
