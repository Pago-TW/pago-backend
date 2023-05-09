package tw.pago.pagobackend.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.NotificationDao;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.service.NotificationService;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationDao notificationDao;

  @Override
  public List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto) {

    List<Notification> notificationList = notificationDao.getNotificationList(listQueryParametersDto);
    return notificationList;
  }

  @Override
  public Integer countNotification(ListQueryParametersDto listQueryParametersDto) {
    return notificationDao.countNotification(listQueryParametersDto);
  }
}
