package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.service.BidService;

@RestController
public class BidController {

  @Autowired
  private BidService bidService;

  @PostMapping("/orders/{orderId}/bids")
  public ResponseEntity<Bid> createBid(@PathVariable Integer orderId,
      @RequestBody @Valid CreateBidRequestDto createBidRequestDto) {

    createBidRequestDto.setOrderId(orderId);
    Integer bidId = bidService.createBid(createBidRequestDto);
    Bid bid = bidService.getBidById(bidId);

    return ResponseEntity.status(HttpStatus.CREATED).body(bid);
  }


  @GetMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<Bid> getBigById(@PathVariable Integer orderId,
      @PathVariable Integer bidId) {

    Bid bid = bidService.getBidById(bidId);
    return ResponseEntity.status(HttpStatus.OK).body(bid);
  }
}
