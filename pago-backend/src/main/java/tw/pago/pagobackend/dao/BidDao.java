package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidDao {

  void createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  void deleteBidById(Integer bidId);

}
