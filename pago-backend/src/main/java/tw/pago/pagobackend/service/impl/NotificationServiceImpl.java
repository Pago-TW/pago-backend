package tw.pago.pagobackend.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.constant.NotificationTypeEnum;
import tw.pago.pagobackend.dao.NotificationDao;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.NotificationResponseDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.service.NotificationService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationDao notificationDao;
  private final UuidGenerator uuidGenerator;

  @Override
  public Notification createNotification(
      CreateNotificationRequestDto createNotificationRequestDto) {
    String notificationId = uuidGenerator.getUuid();


    createNotificationRequestDto.setNotificationId(notificationId);

    notificationDao.createNotification(createNotificationRequestDto);

    Notification notification = notificationDao.getNotificationById(notificationId);
    return notification;
  }

  @Override
  public Notification getNotificationById(String notificationId) {
    return notificationDao.getNotificationById(notificationId);
  }

  @Override
  public NotificationResponseDto getNotificationResponseDtoByNotification(
      Notification notification) {
    NotificationTypeEnum notificationType = notification.getNotificationType();

    if (notificationType.equals(NotificationTypeEnum.ORDER)) {

    }


    return null;
  }

  @Override
  public List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto) {

    List<Notification> notificationList = notificationDao.getNotificationList(listQueryParametersDto);
    return notificationList;
  }

  @Override
  public List<NotificationResponseDto> getNotificationResponseDtoListByNotificationList(
      List<Notification> notificationList) {

    for (Notification notification: notificationList) {

    }


    return null;
  }

  @Override
  public Integer countNotification(ListQueryParametersDto listQueryParametersDto) {
    return notificationDao.countNotification(listQueryParametersDto);
  }
}
