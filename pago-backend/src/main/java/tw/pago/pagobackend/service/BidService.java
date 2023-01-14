package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidService {

  Integer createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(Integer bidId);

}
