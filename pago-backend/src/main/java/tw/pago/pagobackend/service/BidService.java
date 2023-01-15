package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidService {

  Bid createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  void deleteBidById(Integer bidId);

}
