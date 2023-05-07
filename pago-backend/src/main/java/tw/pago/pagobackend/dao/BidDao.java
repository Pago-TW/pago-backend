package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidDao {

  void createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  Bid getBidByOrderIdAndBidId(String orderId, String bidId);

  Bid getChosenBidByOrderId(String orderId);

  Bid getBidByShopperIdAndOrderId(String shopperId, String orderId);

  void deleteBidById(String bidId);

  void deleteBidsByOrderId(String orderId);

  void deleteBidByOrderIdAndBidStatus(String orderId, BidStatusEnum bidStatus);

  void updateBid(UpdateBidRequestDto updateBidRequestDto);

  void chooseBid(Bid bid);

  List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto);

  List<Bid> getBidListByTripId(String tripId);

  Integer countBid(ListQueryParametersDto listQueryParametersDto);

  Integer countBidByTripId(String tripId);


}
