package tw.pago.pagobackend.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.pago.pagobackend.assembler.ReviewAssembler;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
// import tw.pago.pagobackend.dto.UpdateReviewRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.ReviewResponseDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@Validated
@AllArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final ReviewAssembler reviewAssembler;
  private final OrderService orderService;

  @PostMapping("/orders/{orderId}/reviews")
  public ResponseEntity<?> createReview(
      @PathVariable String orderId, @RequestParam("file") List<MultipartFile> files, 
      @Valid @RequestParam("data") String createReviewRequestDtoString) throws JsonMappingException, JsonProcessingException {

    Order order = orderService.getOrderById(orderId);
    String consumerId = order.getConsumerId();
    String shopperId = Optional.ofNullable(order.getShopper())
        .map(shopper -> shopper.getUserId())
        .orElse(null);


    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

    if (!(currentLoginUserId.equals(consumerId) || currentLoginUserId.equals(shopperId))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have no permission");
    }

    if (shopperId == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This order have no chosenShopper");
    }



    ObjectMapper objectMapper = new ObjectMapper();
    CreateReviewRequestDto createReviewRequestDto = objectMapper.readValue(createReviewRequestDtoString, CreateReviewRequestDto.class);

    // Set Parameter to DTO
    createReviewRequestDto.setOrder(order);
    createReviewRequestDto.setOrderId(orderId);
    createReviewRequestDto.setCreatorId(currentLoginUserId);

    // Create Review
    Review review = reviewService.createReview(files, createReviewRequestDto);

    // Convert to Dto by assembler
    ReviewResponseDto reviewResponseDto = reviewAssembler.assemble(review);

    return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponseDto);
  }

  @GetMapping("/reviews/{reviewId}")
  public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable String reviewId) {

    Review review = reviewService.getReviewById(reviewId);
    ReviewResponseDto reviewResponseDto = reviewAssembler.assemble(review);
    return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDto);
  }


  @GetMapping("/reviews")
  public ResponseEntity<ListResponseDto<ReviewResponseDto>> getReviewList(
      @RequestParam(required = true) String userId,
      @RequestParam(required = false) ReviewTypeEnum type,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {


    // Set Query Parameters
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .targetId(userId) // Get the review list for this user
        .reviewType(type) // Set review type {FOR_SHOPPER / FOR_CONSUMER}
        .build();

    // Get ReviewList
    List<Review> reviewList = reviewService.getReviewList(listQueryParametersDto);

    // Convert to Dto by assembler
    List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();
    for (Review review: reviewList) {
      reviewResponseDtoList.add(reviewAssembler.assemble(review));
    }

    // Count total Review with Filter condition
    Integer total = reviewService.countReview(listQueryParametersDto);


    // Set data to ListResponseDto
    ListResponseDto<ReviewResponseDto> reviewListResponseDto = ListResponseDto.<ReviewResponseDto>builder()
        .total(total)
        .startIndex(startIndex)
        .size(size)
        .data(reviewResponseDtoList)
        .build();


    return ResponseEntity.status(HttpStatus.OK).body(reviewListResponseDto);
  }


  // @PatchMapping("/users/{userId}/orders/{orderId}/reviews/{reviewId}")
  // public ResponseEntity<Review> updateReview(@PathVariable String userId, @PathVariable String orderId, @PathVariable String reviewId, 
  // @RequestBody UpdateReviewRequestDto updateReviewRequestDto) {

  //   // Check if the Review to be updated exists
  //   Review review = reviewService.getReviewById(reviewId);
  //   if (review == null) {
  //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(review);
  //   }

  //   // Set Parameter to DTO
  //   updateReviewRequestDto.setOrderId(orderId);
  //   if (updateReviewRequestDto.getReviewType().equals(ReviewTypeEnum.FOR_CONSUMER)) {
  //     updateReviewRequestDto.setShopperId(userId);
  //   } else {
  //     updateReviewRequestDto.setConsumerId(userId);
  //   }

  //   // update Review
  //   Review updatedReview = reviewService.updateReview(updateReviewRequestDto);

  //   return ResponseEntity.status(HttpStatus.OK).body(updatedReview);
  // }
}
