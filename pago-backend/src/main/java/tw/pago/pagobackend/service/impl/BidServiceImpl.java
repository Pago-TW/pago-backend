package tw.pago.pagobackend.service.impl;

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
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.EntityPropertyUtil;
import tw.pago.pagobackend.util.UuidGenerator;


@Component
public class BidServiceImpl implements BidService {

  @Autowired
  private BidDao bidDao;
  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private TripService tripService;
  @Autowired
  private UserService userService;
  @Autowired
  private ReviewService reviewService;
  @Autowired
  private BidAssembler bidAssembler;

  @Override
  public Bid createBid(CreateBidRequestDto createBidRequestDto) {

    // Init UUID & Set UUID
    String uuid = uuidGenerator.getUuid();
    createBidRequestDto.setBidId(uuid);
    createBidRequestDto.setBidStatus(BidStatusEnum.NOT_CHOSEN);
    // Create bid
    bidDao.createBid(createBidRequestDto);
    Bid bid = bidDao.getBidById(uuid);

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
