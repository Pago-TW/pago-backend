package tw.pago.pagobackend.controller;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.JwtTokenProvider;

@RestController
@Validated
public class BidController {

  @Autowired
  private BidService bidService;
  @Autowired
  private JwtTokenProvider jwtTokenProvider;
  @Autowired
  private BidAssembler bidAssembler;
  @Autowired
  private CurrentUserInfoProvider currentUserInfoProvider;

  @PostMapping("/orders/{orderId}/bids")
  public ResponseEntity<Bid> createBid(@PathVariable String orderId,
      @RequestBody @Valid CreateBidRequestDto createBidRequestDto) {

    createBidRequestDto.setOrderId(orderId);
    Bid bid = bidService.createBid(createBidRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(bid);
  }


  @GetMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<BidResponseDto> getBidById(@PathVariable String orderId,
      @PathVariable String bidId) {


    // Get Bid
    Bid bid = bidService.getBidById(bidId);

    // Assemble DTO
    BidResponseDto bidResponseDto = bidAssembler.assemble(bid);


    return ResponseEntity.status(HttpStatus.OK).body(bidResponseDto);
  }


  @DeleteMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<?> deleteBidById(@PathVariable String orderId, @PathVariable String bidId) {
    bidService.deleteBidById(bidId);
    String s = UUID.randomUUID().toString().replaceAll("-", "");
    System.out.println("UUID: " + s);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<Bid> updateBid(@PathVariable String orderId, @PathVariable String bidId, 
  @RequestBody @Valid UpdateBidRequestDto updateBidRequestDto) {

    updateBidRequestDto.setOrderId(orderId);
    updateBidRequestDto.setBidId(bidId);
    bidService.updateBid(updateBidRequestDto);

    Bid updatedBid = bidService.getBidById(bidId);
    return ResponseEntity.status(HttpStatus.OK).body(updatedBid);
  }


  @GetMapping("/orders/{orderId}/bids")
  public ResponseEntity<ListResponseDto<Bid>> getBidList(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .search(search)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();


    List<Bid> bidList = bidService.getBidList(listQueryParametersDto);
    Integer total = bidService.countBid(listQueryParametersDto);

    ListResponseDto<Bid> bidListResponseDto = ListResponseDto.<Bid>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(bidList)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(bidListResponseDto);

  }
}
