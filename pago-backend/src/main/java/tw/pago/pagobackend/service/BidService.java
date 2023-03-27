package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidService {

  Bid createBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  void deleteBidById(String bidId);

  void updateBid(UpdateBidRequestDto updateBidRequestDto);

  List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto);

  Integer countBid(ListQueryParametersDto listQueryParametersDto);

  BidResponseDto getBidResponseById(String bidId);
}
