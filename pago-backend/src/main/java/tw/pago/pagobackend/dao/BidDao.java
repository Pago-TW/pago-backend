package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateBidRequestDto;

public interface BidDao {

  Integer createBid(CreateBidRequestDto createBidRequestDto);

}
