package tw.pago.pagobackend.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.constant.NotificationTypeEnum;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.CreateNotificationUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.service.NotificationService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Validated
@RestController
@AllArgsConstructor
public class NotificationController {
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final NotificationService notificationService;

  @PostMapping("/notifications")
  public ResponseEntity<Notification> createNotification(@RequestBody @Validated CreateNotificationRequestDto createNotificationRequestDto) {
    Notification notification = notificationService.createNotification(createNotificationRequestDto);



    return ResponseEntity.status(HttpStatus.CREATED).body(notification);
  }


  @PostMapping("/notification-user-mapping")
  public ResponseEntity<?> createNotificationUserMapping(@RequestBody @Valid
      CreateNotificationUserMappingRequestDto createNotificationUserMappingRequestDto) {

    notificationService.createNotificationUserMapping(createNotificationUserMappingRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("");
  }


  @GetMapping("/notifications")
  public ResponseEntity<?> getNotificationList(
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) NotificationTypeEnum type,
      @RequestParam(defaultValue = "0") Integer startIndex,
      @RequestParam(defaultValue = "25") Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();


    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .userId(userId != null? userId : currentLoginUserId)
        .notificationType(type)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

   List<Notification> notificationList = notificationService.getNotificationList(listQueryParametersDto);
   Integer total = notificationService.countNotification(listQueryParametersDto);

    ListResponseDto<Notification> notificationListResponseDto = ListResponseDto.<Notification>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(notificationList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(notificationListResponseDto);
  }


  @PatchMapping("/notifications/{notificationId}/read")
  public ResponseEntity<?> markNotificationAsRead(@PathVariable String notificationId) {
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    notificationService.markNotificationAsRead(notificationId, currentLoginUserId);

    return ResponseEntity.status(HttpStatus.OK).body("Mark Notification as read successfully");
  }
}
