package tw.pago.pagobackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
// import tw.pago.pagobackend.dto.UpdateReviewRequestDto;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
public class ReviewController {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private CurrentUserInfoProvider currentUserInfoProvider;

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
  public ResponseEntity<Review> getReviewById(@PathVariable String reviewId) {
    Review review = reviewService.getReviewById(reviewId);
    return ResponseEntity.status(HttpStatus.OK).body(review);
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
