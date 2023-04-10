package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidDao {

  void createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  Bid getBidByOrderIdAndBidId(String orderId, String bidId);

  void deleteBidById(String bidId);

  void updateBid(UpdateBidRequestDto updateBidRequestDto);

  void chooseBid(Bid bid);

  List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto);

  Integer countBid(ListQueryParametersDto listQueryParametersDto);

  Integer countBidByTripId(String tripId);


}
