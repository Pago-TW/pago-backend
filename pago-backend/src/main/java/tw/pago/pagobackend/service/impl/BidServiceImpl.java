package tw.pago.pagobackend.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.assembler.BidAssembler;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.BidCreatorReviewDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderAndOrderItemRequestDto;
import tw.pago.pagobackend.dto.UpdateOrderItemDto;
import tw.pago.pagobackend.exception.IllegalStatusTransitionException;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
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

  @Override
  public Bid createOrUpdateBid(CreateBidRequestDto createBidRequestDto) {

    // Get the trip by its ID
    Trip trip = tripService.getTripById(createBidRequestDto.getTripId());

    // Check if a bid already exists for the shopper and order
    Bid existingBid = bidDao.getBidByShopperIdAndOrderId(trip.getShopperId(),
        createBidRequestDto.getOrderId());

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

      // Get the order by its ID
      String orderId = createBidRequestDto.getOrderId();
      Order order = orderService.getOrderById(orderId);

      // Update the existing bid
      updateBid(updateBidRequestDto, existingBid, order);

      System.out.println("You have already made an offer, so we updated your bid!");
      // Get the updated bid from the database
      Bid updatedBid = bidDao.getBidById(existingBid.getBidId());

      return updatedBid;
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

    // Send the email notification
    String orderId = createBidRequestDto.getOrderId();
    Order order = orderService.getOrderById(orderId);
    sendPlaceBidEmail(bid, order);

    System.out.println("......Email sent! (bid successfully created)");

    return bid;
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




    /*
      * Send email to the bidder
      * Current user: consumer
     * To: Bidder
     * From: pago
     * Subject: Pago 訂單出價通知
     * Body: 親愛的XXX您好，感謝您使用Pago的服務
     */

    // Send email to the bidder
    sendBidChosenEmail(bidId, orderId);

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
    // Get bidder's name
    String bidderName = currentUserInfoProvider.getCurrentLoginUser().getFirstName();

    // Get the order creator's email
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
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
    String emailBody = String.format(" %s 已於 %s 在您的訂單 %s 更新出價：%s %s<br>訂單編號：%s",
        bidderName, date, orderItemName, currency, bidAmount, orderSerialNumber);

    // Prepare the email content
    EmailRequestDto emailRequestDto = new EmailRequestDto();
    emailRequestDto.setTo(orderCreatorEmail);
    emailRequestDto.setSubject("【Pago 訂單出價更新通知】" + orderItemName);
    emailRequestDto.setBody(emailBody);
    emailRequestDto.setRecipientName(orderCreatorName);


    // Send the email
    sesEmailService.sendEmail(emailRequestDto);
  }


  private void sendPlaceBidEmail(Bid bid, Order order) {
    // Get bidder's name
    String bidderName = currentUserInfoProvider.getCurrentLoginUser().getFirstName();

    // Get the order creator's email
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
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
    String emailBody = String.format(" %s 已於 %s 在您的訂單 %s 出價：%s %s<br>訂單編號：$s",
        orderCreatorName, bidderName, date, orderItemName, currency, bidAmount, orderSerialNumber);

    // Prepare the email content
    EmailRequestDto emailRequestDto = new EmailRequestDto();
    emailRequestDto.setTo(orderCreatorEmail);
    emailRequestDto.setSubject("【Pago 訂單出價通知】" + orderItemName);
    emailRequestDto.setRecipientName(orderCreatorName);
    emailRequestDto.setBody(emailBody);

    // Send the email
    sesEmailService.sendEmail(emailRequestDto);
  }


  private void sendBidChosenEmail(String bidId, String orderId) {
    // Get bidder's name and email
    BidResponseDto bidResponseDto = getBidResponseById(bidId);
    User bidder = userService.getUserById(bidResponseDto.getCreator().getUserId());
    String bidderName = bidder.getFirstName();
    String bidderEmail = bidder.getEmail();

    // Get the order item name
    Order order = orderService.getOrderById(orderId);
    String orderItemName = order.getOrderItem().getName();
    String orderSerialNumber = order.getSerialNumber();
    // Get the user name
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
    String orderCreatorName = orderCreator.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String emailBody = String.format("您於 %s 訂單的出價已在 %s 被 %s 選中！請前往 Pago 查看詳情<br>訂單編號：%s",
        orderItemName, date, orderCreatorName, orderSerialNumber);

    // Prepare the email content
    EmailRequestDto emailRequestDto = new EmailRequestDto();
    emailRequestDto.setTo(bidderEmail);
    emailRequestDto.setSubject("【Pago 訂單出價通知】" + orderItemName);
    emailRequestDto.setRecipientName(bidderName);
    emailRequestDto.setBody(emailBody);

    // Send the email notification
    sesEmailService.sendEmail(emailRequestDto);
    System.out.println("......Email sent! (bid chosen)");
  }

}
