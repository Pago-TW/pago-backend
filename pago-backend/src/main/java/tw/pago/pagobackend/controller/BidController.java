package tw.pago.pagobackend.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.dto.BidOperationResultDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.UpdateBidRequestDto;
import tw.pago.pagobackend.exception.AccessDeniedException;
import tw.pago.pagobackend.exception.InvalidDeliveryDateException;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.PaymentService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class BidController {
  private static final String NO_PERMISSION_MESSAGE = "You have no permission";

  private final BidService bidService;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final OrderService orderService;
  private final PaymentService paymentService;
  private final TripService tripService;
  

  @PostMapping("/orders/{orderId}/bids")
  public ResponseEntity<Object> createBid(@PathVariable String orderId,
      @RequestBody @Valid CreateBidRequestDto createBidRequestDto) {
    Order order = orderService.getOrderById(orderId);
    String orderCreatorId = order.getConsumerId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    // Check if the current logged-in user is the creator of the order.
    // If so, do not allow them to place a bid on their own order.
    if (currentLoginUserId.equals(orderCreatorId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("It is not allowed to place bid in your own order");
    }

    // Check if the arrival city in the trip matches the destination city in the order
    Trip trip = tripService.getTripById(createBidRequestDto.getTripId());
    log.info("trip.getToCity(): " + trip.getToCity());
    log.info("order.getDestinationCity(): " + order.getDestinationCity());
    if (!trip.getToCity().equals(order.getDestinationCity())) {
      log.info("Does not match");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The arrival city of the trip must match the destination city of the order");
    }
    log.info("Matches");
    
    createBidRequestDto.setOrderId(orderId);
    // Call the createOrUpdateBid service method to either create a new bid or update an existing one.
    try {
      BidOperationResultDto bidOperationResultDto = bidService.createOrUpdateBid(createBidRequestDto);
      // Check if the bid was created or updated, and return the appropriate HTTP status code.
      if (bidOperationResultDto.isCreated()) {
        // The bid was created, return HTTP status 201 Created.
        return ResponseEntity.status(HttpStatus.CREATED).body(bidOperationResultDto.getBid());
      } else {
        // The bid was updated, return HTTP status 200 OK.
        return ResponseEntity.status(HttpStatus.OK).body(bidOperationResultDto.getBid());
      }
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (InvalidDeliveryDateException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }


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
  public ResponseEntity<String> deleteBidById(@PathVariable String bidId) {

    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    String bidCreatorId = bidResponseDto.getCreator().getUserId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    if (!bidCreatorId.equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(NO_PERMISSION_MESSAGE);
    }


    bidService.deleteBidById(bidId);


    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/bids/{bidId}")
  public ResponseEntity<Bid> updateBid(@PathVariable String bidId,
  @RequestBody @Valid UpdateBidRequestDto updateBidRequestDto) {

    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    String bidCreatorId = bidResponseDto.getCreator().getUserId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    if (!bidCreatorId.equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    updateBidRequestDto.setBidId(bidId);
    bidService.updateBid(updateBidRequestDto);

    Bid updatedBid = bidService.getBidById(bidId);
    return ResponseEntity.status(HttpStatus.OK).body(updatedBid);
  }

  // Consumer chooses a bid
  @PatchMapping("/bids/{bidId}/choose")
  public ResponseEntity<String> chooseBid(@PathVariable String bidId) {
    BidResponseDto bidResponseDto = bidService.getBidResponseById(bidId);
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    Order order = orderService.getOrderById(bidResponseDto.getOrderId());
    String orderCreatorId = order.getConsumerId();

    if (!orderCreatorId.equals(currentLoginUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(NO_PERMISSION_MESSAGE);
    }

    if (!order.getOrderStatus().equals(OrderStatusEnum.REQUESTED)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Order Status is not REQUESTED");
    }

    if (bidResponseDto.getBidStatus().equals(BidStatusEnum.IS_CHOSEN)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The bid has been chosen");
    }

    // Direct consumer to ECpay payment page, if payment success, ECpay will callback and update orderStatus & bidStatus
    String aioCheckoutALLForm = paymentService.ecpayCheckout(bidId);

    return ResponseEntity.status(HttpStatus.OK).body(aioCheckoutALLForm);

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
