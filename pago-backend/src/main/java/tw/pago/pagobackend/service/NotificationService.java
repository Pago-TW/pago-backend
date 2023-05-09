package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.CreateNotificationUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.NotificationResponseDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.model.NotificationUserMapping;

public interface NotificationService {

  Notification createNotification(CreateNotificationRequestDto createNotificationRequestDto);

  NotificationUserMapping createNotificationUserMapping(CreateNotificationUserMappingRequestDto createNotificationUserMappingRequestDto);

  Notification getNotificationById(String notificationId);

  NotificationResponseDto getNotificationResponseDtoByNotification(Notification notification);

  List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto);

  List<NotificationResponseDto> getNotificationResponseDtoListByNotificationList(List<Notification> notificationList);

  Integer countNotification(ListQueryParametersDto listQueryParametersDto);

}
