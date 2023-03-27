package tw.pago.pagobackend.controller;


import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
import tw.pago.pagobackend.dto.ReviewResponseDto;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.service.ReviewService;
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
  public ResponseEntity<ReviewResponseDto> createReview(
      @PathVariable String orderId,
      @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto) {

    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();


    // Set Parameter to DTO
    createReviewRequestDto.setOrderId(orderId);
    createReviewRequestDto.setCreatorId(currentLoginUserId);

    // Create Review
    Review review = reviewService.createReview(createReviewRequestDto);

    // Convert to Dto by assembler
    ReviewResponseDto reviewResponseDto = reviewAssembler.assemble(review);

    return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponseDto);
  }

  @GetMapping("/users/{userId}/reviews/{reviewId}")
  public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable String reviewId) {

    Review review = reviewService.getReviewById(reviewId);
    ReviewResponseDto reviewResponseDto = reviewAssembler.assemble(review);
    return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDto);
  }


  @GetMapping("/users/{userId}/reviews")
  public ResponseEntity<ListResponseDto<ReviewResponseDto>> getReviewList(@PathVariable String userId,
      @RequestParam(required = true) ReviewTypeEnum type,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort) {


    // Set Query Parameters
    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .search(search)
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
