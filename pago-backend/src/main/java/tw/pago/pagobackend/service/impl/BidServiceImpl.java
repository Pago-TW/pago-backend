package tw.pago.pagobackend.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.assembler.BidAssembler;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.BidCreatorReviewDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.EmailRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
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
public class BidServiceImpl implements BidService {

  @Autowired
  private BidDao bidDao;
  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private OrderService orderService;
  @Autowired
  private TripService tripService;
  @Autowired
  private UserService userService;
  @Autowired
  private ReviewService reviewService;
  @Autowired
  private BidAssembler bidAssembler;
  @Autowired
  private SesEmailService sesEmailService;
  @Autowired
  private CurrentUserInfoProvider currentUserInfoProvider;

  @Override
  public Bid createBid(CreateBidRequestDto createBidRequestDto) {

    String currency = createBidRequestDto.getCurrency().toString();
    String bidAmount = createBidRequestDto.getBidAmount().toString();

    // Init UUID & Set UUID
    String uuid = uuidGenerator.getUuid();
    createBidRequestDto.setBidId(uuid);
    createBidRequestDto.setBidStatus(BidStatusEnum.NOT_CHOSEN);
    // Create bid
    bidDao.createBid(createBidRequestDto);
    Bid bid = bidDao.getBidById(uuid);

    // Get bidder's name
    String bidderName = currentUserInfoProvider.getCurrentLoginUser().getFirstName();

    // Get the order creator's email
    String orderId = createBidRequestDto.getOrderId();
    Order order = orderService.getOrderById(orderId);
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
    String orderCreatorEmail = orderCreator.getEmail();

    // Get the order item name
    String orderItemName = order.getOrderItem().getName();
    // Get the user name
    String orderCreatorName = orderCreator.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String emailBody = String.format("親愛的%s您好，感謝您使用Pago的服務\n" +
    "%s已於%s在您的訂單 %s 出價：%s%s", 
    orderCreatorName, bidderName, date, orderItemName, currency, bidAmount);

    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(orderCreatorEmail);
    emailRequest.setSubject("【Pago 訂單出價通知】" + orderItemName);
    emailRequest.setBody(emailBody);

    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent!");

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
  }

  @Override
  public void chooseBid(String orderId, String bidId) {
    Bid bid = getBidById(bidId);

    if (bid == null) {
        throw new ResourceNotFoundException(bidId, "Bid not found", bidId);
    }

    bid.setBidStatus(BidStatusEnum.IS_CHOSEN);

    /*
      * Send email to the bidder
      * Current user: consumer
     * To: Bidder
     * From: pago
     * Subject: Pago 訂單出價通知
     * Body: 親愛的XXX您好，感謝您使用Pago的服務
     */

    // Get bidder's name and email
    String tripId = bid.getTripId();
    Trip trip = tripService.getTripById(tripId);
    String bidderId = trip.getShopperId();
    User bidder = userService.getUserById(bidderId);
    String bidderName = bidder.getFirstName();
    String bidderEmail = bidder.getEmail();

    // Get the order item name
    Order order = orderService.getOrderById(orderId);
    String orderItemName = order.getOrderItem().getName();
    // Get the user name
    String orderCreatorId = order.getConsumerId();
    User orderCreator = userService.getUserById(orderCreatorId);
    String orderCreatorName = orderCreator.getFirstName();
    // Get current date
    Date now = new Date();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(now);

    String emailBody = String.format("親愛的%s您好，感謝您使用Pago的服務\n" +
    "您於 %s 訂單的出價已在%s被%s選中！請前往Pago查看詳情",
    bidderName, orderItemName, date, orderCreatorName);

    // Prepare the email content
    EmailRequestDto emailRequest = new EmailRequestDto();
    emailRequest.setTo(bidderEmail);
    emailRequest.setSubject("【Pago 訂單出價通知】" + orderItemName);
    emailRequest.setBody(emailBody);

    // Send the email notification
    sesEmailService.sendEmail(emailRequest);
    System.out.println("......Email sent!");

    bidDao.chooseBid(bid);
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


}
