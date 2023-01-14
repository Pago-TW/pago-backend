package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.service.BidService;

@RestController
public class BidController {

  @Autowired
  private BidService bidService;

  @PostMapping("/orders/{orderId}/bids")
  public ResponseEntity<?> createBid(@PathVariable Integer orderId,
      @RequestBody @Valid CreateBidRequestDto createBidRequestDto) {

    createBidRequestDto.setOrderId(orderId);
    Integer bidId = bidService.createBid(createBidRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(bidId);
  }
}
