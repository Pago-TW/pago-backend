package tw.pago.pagobackend.controller;


import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.modelmapper.ModelMapper;
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
import tw.pago.pagobackend.assembler.ReviewAssembler;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
// import tw.pago.pagobackend.dto.UpdateReviewRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.OrderDto;
import tw.pago.pagobackend.dto.ReviewCreatorDto;
import tw.pago.pagobackend.dto.ReviewOrderDto;
import tw.pago.pagobackend.dto.ReviewResponseDto;
import tw.pago.pagobackend.dto.ReviewTargetDto;
import tw.pago.pagobackend.dto.UserDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@Validated
public class ReviewController {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private CurrentUserInfoProvider currentUserInfoProvider;
  @Autowired
  private ReviewAssembler reviewAssembler;

  @PostMapping("/orders/{orderId}/reviews")
  public ResponseEntity<Review> createReview(
      @PathVariable String orderId,
      @RequestBody CreateReviewRequestDto createReviewRequestDto) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();


    // Set Parameter to DTO
    createReviewRequestDto.setOrderId(orderId);
    createReviewRequestDto.setCreatorId(currentLoginUserId);

    // Create Review
    Review review = reviewService.createReview(createReviewRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(review);
  }

  @GetMapping("/users/{userId}/reviews/{reviewId}")
  public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable String reviewId) {

    Review review = reviewService.getReviewById(reviewId);
    ReviewResponseDto reviewResponseDto = reviewAssembler.assemble(review);
    return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDto);
  }


//  @GetMapping("/users/{userId}/reviews")
//  public ResponseEntity<ListResponseDto<Review>> getReviewList(@PathVariable String userId,
//      @RequestParam(required = false) String search,
//      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
//      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
//      @RequestParam(defaultValue = "create_date") String orderBy,
//      @RequestParam(defaultValue = "DESC") String sort) {
//
//    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
//        .search(search)
//        .startIndex(startIndex)
//        .size(size)
//        .orderBy(orderBy)
//        .sort(sort)
//        .build();
//
//    List<Review> reviewList = reviewService.getReviewList(listQueryParametersDto);
//
//    Integer total = reviewList.countReview(listQueryParametersDto);
//
//    ListResponseDto<Review> reviewListResponseDto = ListResponseDto.<Review>builder()
//        .total(total)
//        .startIndex(startIndex)
//        .size(size)
//        .data(reviewList)
//        .build();
//
//
//    return ResponseEntity.status(HttpStatus.OK).body(reviewListResponseDto);
//  }


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
