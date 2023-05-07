package tw.pago.pagobackend.service;

import java.util.List;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.dto.BidOperationResultDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;

public interface BidService {

  BidOperationResultDto createOrUpdateBid(CreateBidRequestDto createBidRequestDto);

  Bid getBidById(String bidId);

  Bid getBidByOrderIdAndBidId(String orderId, String bidId);

  Bid getChosenBidByOrderId(String orderId);

  void deleteBidById(String bidId);

  void deleteBidsByOrderId(String orderId);

  void deleteBidByOrderIdAndBidStatus(String orderId, BidStatusEnum bidStatus);

  void updateBid(UpdateBidRequestDto updateBidRequestDto);

  void updateBid(UpdateBidRequestDto updateBidRequestDto, Bid existingBid, Order order);

  void chooseBid(String orderId, String bidId);

  List<Bid> getBidList(ListQueryParametersDto listQueryParametersDto);

  Integer countBid(ListQueryParametersDto listQueryParametersDto);

  BidResponseDto getBidResponseById(String bidId);

  BidResponseDto getBidResponseByBid(Bid bid);

  BidResponseDto getBidResponseByOrderIdAndBidId(String orderId, String bidId);

  List<BidResponseDto> getBidResponseDtoList(ListQueryParametersDto listQueryParametersDto);
}
