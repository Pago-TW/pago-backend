package tw.pago.pagobackend.controller;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    Bid bid = bidService.createBid(createBidRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(bid);
  }


  @GetMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<Bid> getBidById(@PathVariable Integer orderId,
      @PathVariable String bidId) {

    Bid bid = bidService.getBidById(bidId);
    return ResponseEntity.status(HttpStatus.OK).body(bid);
  }


  @DeleteMapping("/orders/{orderId}/bids/{bidId}")
  public ResponseEntity<?> deleteBidById(@PathVariable Integer orderId, @PathVariable Integer bidId) {
    bidService.deleteBidById(bidId);
    String s = UUID.randomUUID().toString().replaceAll("-", "");
    System.out.println("UUID: " + s);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
