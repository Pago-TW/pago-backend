package tw.pago.pagobackend.controller;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.assembler.BidAssembler;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.JwtTokenProvider;

@RestController
@Validated
@AllArgsConstructor
public class BidController {

  private final BidService bidService;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final OrderService orderService;

  @PostMapping("/orders/{orderId}/bids")
  public ResponseEntity<Bid> createBid(@PathVariable String orderId,
      @RequestBody @Valid CreateBidRequestDto createBidRequestDto) {

    createBidRequestDto.setOrderId(orderId);
    Bid bid = bidService.createBid(createBidRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(bid);
  }


  @GetMapping("/bids/{bidId}")
  public ResponseEntity<BidResponseDto> getBidById(
      @PathVariable String bidId) {


    // Get Bid
    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);

    return ResponseEntity.status(HttpStatus.OK).body(bidResponseDto);
  }

  @GetMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<BidResponseDto> getBidById(@PathVariable String orderId,
      @PathVariable String bidId) {


    // Get Bid
    BidResponseDto bidResponseDto = bidService.getBidResponseByOrderIdAndBidId(orderId, bidId);

    return ResponseEntity.status(HttpStatus.OK).body(bidResponseDto);
  }


  @DeleteMapping("/bids/{bidId}")
  public ResponseEntity<?> deleteBidById(@PathVariable String bidId) {

    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    String bidCreatorId = bidResponseDto.getCreator().getUserId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    if (!bidCreatorId.equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }


    bidService.deleteBidById(bidId);
    String s = UUID.randomUUID().toString().replaceAll("-", "");
    System.out.println("UUID: " + s);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/bids/{bidId}")
  public ResponseEntity<?> updateBid(@PathVariable String bidId,
  @RequestBody @Valid UpdateBidRequestDto updateBidRequestDto) {

    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    String bidCreatorId = bidResponseDto.getCreator().getUserId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    if (!bidCreatorId.equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }


    updateBidRequestDto.setBidId(bidId);
    bidService.updateBid(updateBidRequestDto);

    Bid updatedBid = bidService.getBidById(bidId);
    return ResponseEntity.status(HttpStatus.OK).body(updatedBid);
  }

  // Consumer chooses a bid
  @PatchMapping("/bids/{bidId}/choose")
  public ResponseEntity<?> chooseBid(@PathVariable String bidId) {
    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    Order order = orderService.getOrderById(bidResponseDto.getOrderId());
    String orderCreatorId = order.getConsumerId();
    String orderId = bidResponseDto.getOrderId();

    if (!orderCreatorId.equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Order Status is not REQUESTED");
    }


      bidService.chooseBid(orderId, bidId);
      Bid updatedBid = bidService.getBidById(bidId);
      return ResponseEntity.status(HttpStatus.OK).body(updatedBid);
  }


  @GetMapping("/orders/{orderId}/bids")
  public ResponseEntity<ListResponseDto<BidResponseDto>> getBidList(
      @PathVariable String orderId,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    // Set query parameters
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .orderId(orderId)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();


    // Get BidResponseDto list with query parameters
    List<BidResponseDto> bidResponseDtoList = bidService.getBidResponseDtoList(listQueryParametersDto);

    // Calculate the total number of bids that meet the specified query parameters conditions
    Integer total = bidService.countBid(listQueryParametersDto);

    ListResponseDto<BidResponseDto> bidListResponseDto = ListResponseDto.<BidResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(bidResponseDtoList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(bidListResponseDto);

  }
}
