package tw.pago.pagobackend.service.impl;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.BidShopperDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
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
  private UserService userService;
  @Autowired
  private TripService tripService;

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
    Trip bidRelatedTrip = tripService.getTripById(bid.getTripId());
    User bidder = userService.getUserById(bidRelatedTrip.getShopperId());

    BidShopperDto bidShopperDto = BidShopperDto.builder()
        .shopperId(bidder.getUserId())
        .name(bidder.getFullName())
        .avatarUrl(bidder.getAvatarUrl())
//        .review()
        .build();


//    BidResponseDto bidResponseDto = BidResponseDto.builder()
//        .bidId(bid.getBidId())
////        .shopper()
//        .orderId(bid.getOrderId())
//        .tripId(bid.getTripId())
//        .bidAmount(bid.getBidAmount())
//        .currency(bid.getCurrency())
//        .createDate(bid.getCreateDate())
//        .updateDate(bid.getUpdateDate())
//        .bidStatus(bid.getBidStatus())
//        .build();
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
}
