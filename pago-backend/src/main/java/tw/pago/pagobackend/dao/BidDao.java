package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidDao {

  Integer createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(Integer bidId);

}
