package tw.pago.pagobackend.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.assembler.BidAssembler;
import tw.pago.pagobackend.constant.ActionTypeEnum;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.NotificationTypeEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.BidCreatorReviewDto;
import tw.pago.pagobackend.dto.BidOperationResultDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.CreateNotificationRequestDto;
import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderItemDto;
import tw.pago.pagobackend.exception.AccessDeniedException;
import tw.pago.pagobackend.exception.IllegalStatusTransitionException;
import tw.pago.pagobackend.exception.InvalidDeliveryDateException;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Notification;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.NotificationService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.SesEmailService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.EntityPropertyUtil;
import tw.pago.pagobackend.util.UuidGenerator;


@Component
@AllArgsConstructor
public class BidServiceImpl implements BidService {

  private final BidDao bidDao;
  private final UuidGenerator uuidGenerator;
  private final @Lazy OrderService orderService;
  private final TripService tripService;
  private final UserService userService;
  private final ReviewService reviewService;
  private final BidAssembler bidAssembler;
  private final SesEmailService sesEmailService;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public BidOperationResultDto createOrUpdateBid(CreateBidRequestDto createBidRequestDto) {
    String orderId = createBidRequestDto.getOrderId();
    Order order = orderService.getOrderById(orderId);

    // Check whether the order status is REQUESTED
    if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
      throw new AccessDeniedException("OrderId: " + orderId + ", the orderStatus is not REQUESTED, you have no permission to place bid." );
    }


    Trip trip = tripService.getTripById(createBidRequestDto.getTripId());
    Date arrivalDate = trip.getArrivalDate();
    Date lastReceiveItemDate = order.getLatestReceiveItemDate();
    Date latedDeliveryDate = createBidRequestDto.getLatestDeliveryDate();

    // Throws an InvalidDeliveryDateException if the latest delivery date is not within the arrival date and the latest receive item date.
    if (!(latedDeliveryDate.after(arrivalDate) && latedDeliveryDate.before(lastReceiveItemDate))) {
      throw new InvalidDeliveryDateException("DeliveryDate should be after the arrivalDate and before the lastReceiveItemDate.");
    }


    Bid existingBid = bidDao.getBidByShopperIdAndOrderId(trip.getShopperId(),
        createBidRequestDto.getOrderId());

    // Check if a bid already exists for the shopper and order
    if (existingBid != null) {
      // If the bid already exists, create an update request for the existing bid
      UpdateBidRequestDto updateBidRequestDto = UpdateBidRequestDto.builder()
          .bidId(existingBid.getBidId())
          .orderId(createBidRequestDto.getOrderId())
          .bidAmount(createBidRequestDto.getBidAmount())
          .tripId(createBidRequestDto.getTripId())
          .currency(createBidRequestDto.getCurrency())
          .latestDeliveryDate(createBidRequestDto.getLatestDeliveryDate())
          .build();

      // Update the existing bid
      updateBid(updateBidRequestDto, existingBid, order);

      System.out.println("You have already made an offer, so we updated your bid!");
      // Get the updated bid from the database
      Bid updatedBid = bidDao.getBidById(existingBid.getBidId());

      BidOperationResultDto bidOperationResultDto = new BidOperationResultDto();
      bidOperationResultDto.setBid(updatedBid);
      bidOperationResultDto.setCreated(false);

      return bidOperationResultDto;
    }

    // Convert the currency and bid amount to strings
    String currency = createBidRequestDto.getCurrency().toString();
    String bidAmount = createBidRequestDto.getBidAmount().toString();

    // Generate a UUID for the new bid
    String uuid = uuidGenerator.getUuid();

    // Set the bid ID and initial status
    createBidRequestDto.setBidId(uuid);
    createBidRequestDto.setBidStatus(BidStatusEnum.NOT_CHOSEN);

    // Create the new bid in the database
    bidDao.createBid(createBidRequestDto);

    // Get the newly created bid from the database
    Bid bid = bidDao.getBidById(uuid);

    BidOperationResultDto bidOperationResultDto = new BidOperationResultDto();
    bidOperationResultDto.setBid(bid);
    bidOperationResultDto.setCreated(true);

    // Prepare Notification
    CreateNotificationRequestDto createNotificationRequestDto = new CreateNotificationRequestDto();
    createNotificationRequestDto.setContent("有人出價囉");
    createNotificationRequestDto.setActionType(ActionTypeEnum.PLACE_BID);
    createNotificationRequestDto.setNotificationType(NotificationTypeEnum.ORDER);
    User currentLoginUser =  currentUserInfoProvider.getCurrentLoginUser();
    createNotificationRequestDto.setImageUrl(currentLoginUser.getAvatarUrl());
    createNotificationRequestDto.setRedirectUrl("https:google.com/" + orderId);
    Notification notification = notificationService.createNotification(createNotificationRequestDto);

    // Send notification
    notificationService.sendNotification(notification, order.getConsumerId());
    System.out.println("......Notification sent! (bid successfully created)");

    // Send the email notification
    sendPlaceBidEmail(bid, order);

    System.out.println("......Email sent! (bid successfully created)");

    return bidOperationResultDto;
  }



  @Override
  public Bid getBidById(String bidId) {


    Bid bid = bidDao.getBidById(bidId);

    return bid;

  }


  @Override
  public Bid getBidByOrderIdAndBidId(String orderId, String bidId) {

    Bid bid = bidDao.getBidByOrderIdAndBidId(orderId, bidId);

    return bid;
  }

  @Override
  public Bid getChosenBidByOrderId(String orderId) {

    Bid bid = bidDao.getChosenBidByOrderId(orderId);

    return bid;
  }

  @Override
  public void deleteBidById(String bidId) {
    bidDao.deleteBidById(bidId);
  }

  @Override
  public void deleteBidsByOrderId(String orderId) {
    bidDao.deleteBidsByOrderId(orderId);
  }

  @Override
  public void deleteBidByOrderIdAndBidStatus(String orderId, BidStatusEnum bidStatus) {
    bidDao.deleteBidByOrderIdAndBidStatus(orderId, bidStatus);
  }

  @Override
  public void updateBid(UpdateBidRequestDto updateBidRequestDto) {

    Bid oldBid = getBidById(updateBidRequestDto.getBidId());

    if (oldBid == null) {
      throw new ResourceNotFoundException(updateBidRequestDto.getBidId(), " Not found", oldBid.getBidId());
    }

    // Get existing field names in updateDto
    String[] presentPropertyNames = EntityPropertyUtil.getPresentPropertyNames(updateBidRequestDto);

    // Copy properties from oldBid to updateDto, while ignoring the existing fields in updateDto.
    BeanUtils.copyProperties(oldBid, updateBidRequestDto, presentPropertyNames);

    bidDao.updateBid(updateBidRequestDto);

    Bid updatedBid = bidDao.getBidById(updateBidRequestDto.getBidId());
    Order order = orderService.getOrderById(updatedBid.getOrderId());
    sendUpdateBidEmail(updatedBid, order);
    System.out.println("......Email sent! (bid successfully updated)");
  }

  @Override
  public void updateBid(UpdateBidRequestDto updateBidRequestDto, Bid existingBid, Order order) {
    String orderId = order.getOrderId();
    Bid oldBid = getBidById(updateBidRequestDto.getBidId());

    if (oldBid == null) {
      throw new ResourceNotFoundException(updateBidRequestDto.getBidId(), " Not found", oldBid.getBidId());
    }

    // Get existing field names in updateDto
    String[] presentPropertyNames = EntityPropertyUtil.getPresentPropertyNames(updateBidRequestDto);

    // Copy properties from oldBid to updateDto, while ignoring the existing fields in updateDto.
    BeanUtils.copyProperties(oldBid, updateBidRequestDto, presentPropertyNames);

    bidDao.updateBid(updateBidRequestDto);

    Bid updatedBid = bidDao.getBidById(updateBidRequestDto.getBidId());

    // Prepare Notification
    CreateNotificationRequestDto createNotificationRequestDto = new CreateNotificationRequestDto();
    createNotificationRequestDto.setContent("有人更新出價囉");
    createNotificationRequestDto.setActionType(ActionTypeEnum.PLACE_BID);
    createNotificationRequestDto.setNotificationType(NotificationTypeEnum.ORDER);
    User currentLoginUser =  currentUserInfoProvider.getCurrentLoginUser();
    createNotificationRequestDto.setImageUrl(currentLoginUser.getAvatarUrl());
    createNotificationRequestDto.setRedirectUrl("https:google.com/" + orderId);
    Notification notification = notificationService.createNotification(createNotificationRequestDto);

    // Send notification
    notificationService.sendNotification(notification, order.getConsumerId());
    System.out.println("......Notification sent! (bid successfully updated)");

    // Send Email
    sendUpdateBidEmail(updatedBid, order);
    System.out.println("......Email sent! (bid successfully updated)");
  }

  @Override
  @Transactional
  public void chooseBid(String orderId, String bidId)  {
    Bid bid = getBidById(bidId);
    BigDecimal bidAmount = bid.getBidAmount();
    Order order = orderService.getOrderById(orderId);

    if (bid == null) {
        throw new ResourceNotFoundException(bidId, "Bid not found", bidId);
    }

    if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
      throw new IllegalStatusTransitionException("The status of this order is not REQUESTED");
    }

    if (bid.getBidStatus().equals(BidStatusEnum.IS_CHOSEN)) {
      throw new IllegalStatusTransitionException("This bid has been chosen");
    }


    bid.setBidStatus(BidStatusEnum.IS_CHOSEN);



    // Choose bid will update the bidStatus
    bidDao.chooseBid(bid);
    UpdateOrderItemDto updateOrderItemDto = new UpdateOrderItemDto();
    BeanUtils.copyProperties(order.getOrderItem(), updateOrderItemDto);
    UpdateOrderAndOrderItemRequestDto updateOrderAndOrderItemRequestDto = UpdateOrderAndOrderItemRequestDto.builder()
        .travelerFee(bidAmount)
        .orderStatus(OrderStatusEnum.TO_BE_PURCHASED)
        .build();

    // Update orderStatus & travelerFee
    orderService.updateOrderAndOrderItemByOrderId(order, updateOrderAndOrderItemRequestDto, false);

    // Delete all NOT_CHOSEN bids made for the order
    deleteBidByOrderIdAndBidStatus(orderId, BidStatusEnum.NOT_CHOSEN);




    /*
      * Send email to the bidder
      * Current user: consumer
     * To: Bidder
     * From: pago
     * Subject: Pago 訂單出價通知
     * Body: 親愛的XXX您好，感謝您使用Pago的服務
     */

    // Send email to the bidder
    sendBidChosenEmail(bid, order);

  }

  @Override
  public List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto) {


    List<Bid> bidList = bidDao.getBidList(listQueryParametersDto);

    return bidList;
  }


  @Override
  public Integer countBid(ListQueryParametersDto listQueryParametersDto) {
    Integer total = bidDao.countBid(listQueryParametersDto);
    return total;
  }


  @Override
  public BidResponseDto getBidResponseById(String bidId) {

    // Get related data
    Bid bid = getBidById(bidId);
    Trip trip = tripService.getTripById(bid.getTripId());
    User creator = userService.getUserById(trip.getShopperId());

    // Get averageRating & totalReview
    ReviewRatingResultDto reviewRatingResultDto = reviewService.calculateAverageRating(creator.getUserId(), ReviewTypeEnum.FOR_SHOPPER);


    // Set value to bidCreatorReviewDto
    BidCreatorReviewDto bidCreatorReviewDto = new BidCreatorReviewDto();
    bidCreatorReviewDto.setAverageRating(reviewRatingResultDto.getAverageRating());
    bidCreatorReviewDto.setTotalReview(reviewRatingResultDto.getTotalReviews());
    bidCreatorReviewDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);


    // Convert to ResponseDTO
    BidResponseDto bidResponseDto = bidAssembler.assemble(bid, trip, creator, bidCreatorReviewDto);

    return bidResponseDto;
  }

  @Override
  public BidResponseDto getBidResponseByBid(Bid bid) {
    // Get related data
    Trip trip = tripService.getTripById(bid.getTripId());
    User creator = userService.getUserById(trip.getShopperId());

    // Get averageRating & totalReview
    ReviewRatingResultDto reviewRatingResultDto = reviewService.calculateAverageRating(creator.getUserId(), ReviewTypeEnum.FOR_SHOPPER);


    // Set value to bidCreatorReviewDto
    BidCreatorReviewDto bidCreatorReviewDto = new BidCreatorReviewDto();
    bidCreatorReviewDto.setAverageRating(reviewRatingResultDto.getAverageRating());
    bidCreatorReviewDto.setTotalReview(reviewRatingResultDto.getTotalReviews());
    bidCreatorReviewDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);


    // Convert to ResponseDTO
    BidResponseDto bidResponseDto = bidAssembler.assemble(bid, trip, creator, bidCreatorReviewDto);

    return bidResponseDto;
  }

  @Override
  public BidResponseDto getBidResponseByOrderIdAndBidId(String orderId, String bidId) {
    // Get related data
    Bid bid = bidDao.getBidByOrderIdAndBidId(orderId, bidId);


    Trip trip = tripService.getTripById(bid.getTripId());
    User creator = userService.getUserById(trip.getShopperId());

    // Get averageRating & totalReview
    ReviewRatingResultDto reviewRatingResultDto = reviewService.calculateAverageRating(creator.getUserId(), ReviewTypeEnum.FOR_SHOPPER);

    // Set value to bidCreatorReviewDto
    BidCreatorReviewDto bidCreatorReviewDto = new BidCreatorReviewDto();
    bidCreatorReviewDto.setAverageRating(reviewRatingResultDto.getAverageRating());
    bidCreatorReviewDto.setTotalReview(reviewRatingResultDto.getTotalReviews());
    bidCreatorReviewDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);

    // Convert to ResponseDTO
    BidResponseDto bidResponseDto = bidAssembler.assemble(bid, trip, creator, bidCreatorReviewDto);

    return bidResponseDto;
  }



  @Override
  public List<BidResponseDto> getBidResponseDtoList(ListQueryParametersDto listQueryParametersDto) {

    List<Bid> bidList = getBidList(listQueryParametersDto);

    // Assemble bid list into BidResponseDto list
    List<BidResponseDto> bidResponseDtoList = bidList.stream()
        .map(bid -> getBidResponseById(bid.getBidId()))
        .collect(Collectors.toList());



    return bidResponseDtoList;
  }


  private void sendUpdateBidEmail(Bid bid, Order order) {
    String contentTitle = "訂單出價更新通知";

    // Get bidder's name
    String bidderName = currentUserInfoProvider.getCurrentLoginUser().getFirstName();

    // Get the order creator's email
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);
    String orderCreatorEmail = orderCreator.getEmail();

    // Get the order item name
    String orderItemName = order.getOrderItem().getName();
    // Get the user name
    String orderCreatorName = orderCreator.getFirstName();

    String orderSerialNumber = order.getSerialNumber();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String currency = bid.getCurrency().toString();
    String bidAmount = bid.getBidAmount().toString();
    String emailBody = String.format(" %s 已於 %s 在您的訂單 <a href=\"%s\">%s</a> 更新出價：<b>%s %s</b><br><br>訂單編號：%s",
        bidderName, date, orderUrl, orderItemName, currency, bidAmount, orderSerialNumber);

    // Prepare the email content
    EmailRequestDto emailRequestDto = new EmailRequestDto();
    emailRequestDto.setTo(orderCreatorEmail);
    emailRequestDto.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequestDto.setContentTitle(contentTitle);
    emailRequestDto.setBody(emailBody);
    emailRequestDto.setRecipientName(orderCreatorName);


    // Send the email
    sesEmailService.sendEmail(emailRequestDto);
  }


  private void sendPlaceBidEmail(Bid bid, Order order) {
    String contentTitle = "訂單出價通知";

    // Get bidder's name
    String bidderName = currentUserInfoProvider.getCurrentLoginUser().getFirstName();

    // Get the order creator's email
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);
    String orderCreatorEmail = orderCreator.getEmail();

    // Get the order item name
    String orderItemName = order.getOrderItem().getName();
    // Get the user name
    String orderCreatorName = orderCreator.getFirstName();
    // Get orderSerialNumber
    String orderSerialNumber = order.getSerialNumber();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String currency = bid.getCurrency().toString();
    String bidAmount = bid.getBidAmount().toString();
    String emailBody = String.format(" %s 已於 %s 在您的訂單 <a href=\"%s\">%s</a> 出價：<b>%s %s</b><br><br>訂單編號：%s",
        bidderName, date, orderUrl, orderItemName, currency, bidAmount, orderSerialNumber);

    // Prepare the email content
    EmailRequestDto emailRequestDto = new EmailRequestDto();
    emailRequestDto.setTo(orderCreatorEmail);
    emailRequestDto.setSubject("【Pago "+ contentTitle +"】" + orderItemName);
    emailRequestDto.setContentTitle(contentTitle);
    emailRequestDto.setRecipientName(orderCreatorName);
    emailRequestDto.setBody(emailBody);

    // Send the email
    sesEmailService.sendEmail(emailRequestDto);
  }


  private void sendBidChosenEmail(Bid bid, Order order) {
    String contentTitle = "訂單出價通知";
    String bidId = bid.getBidId();
    String orderId = order.getOrderId();
    String orderUrl = String.format("https://pago-app.me/ordrs/%s", orderId);

    // Get bidder's name and email
    BidResponseDto bidResponseDto = getBidResponseByBid(bid);
    User bidder = userService.getUserById(bidResponseDto.getCreator().getUserId());
    String bidderName = bidder.getFirstName();
    String bidderEmail = bidder.getEmail();

    // Get the order item name
    String orderItemName = order.getOrderItem().getName();
    String orderSerialNumber = order.getSerialNumber();
    // Get the user name
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
    String orderCreatorName = orderCreator.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String emailBody = String.format("您於 <a href=\"%s\">%s</a> 訂單的出價已在 %s 被 %s 選中！請前往 Pago 查看詳情<br><br>訂單編號：%s",
        orderUrl, orderItemName, date, orderCreatorName, orderSerialNumber);

    // Prepare the email content
    EmailRequestDto emailRequestDto = new EmailRequestDto();
    emailRequestDto.setTo(bidderEmail);
    emailRequestDto.setSubject("【Pago " + contentTitle + "】" + orderItemName);
    emailRequestDto.setContentTitle(contentTitle);
    emailRequestDto.setRecipientName(bidderName);
    emailRequestDto.setBody(emailBody);

    // Send the email notification
    sesEmailService.sendEmail(emailRequestDto);
    System.out.println("......Email sent! (bid chosen)");
  }

}
