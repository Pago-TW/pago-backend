package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.Notification;

public interface NotificationService {

  List<Notification> getNotificationList(ListQueryParametersDto listQueryParametersDto);

  Integer countNotification(ListQueryParametersDto listQueryParametersDto);

}
