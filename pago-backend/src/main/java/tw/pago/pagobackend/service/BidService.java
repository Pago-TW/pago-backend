package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;

public interface BidService {

  Bid createOrUpdateBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  Bid getBidByOrderIdAndBidId(String orderId, String bidId);

  Bid getChosenBidByOrderId(String orderId);

  void deleteBidById(String bidId);

  void updateBid(UpdateBidRequestDto updateBidRequestDto);

  void chooseBid(String orderId, String bidId);

  List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto);

  Integer countBid(ListQueryParametersDto listQueryParametersDto);

  BidResponseDto getBidResponseById(String bidId);

  BidResponseDto getBidResponseByOrderIdAndBidId(String orderId, String bidId);

  List<BidResponseDto> getBidResponseDtoList(ListQueryParametersDto listQueryParametersDto);
}
