package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateBidRequestDto;

public interface BidService {

  Integer createBid(CreateBidRequestDto createBidRequestDto);

}
