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
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.service.ReviewService;

@RestController
public class ReviewController {

  @Autowired
  private ReviewService reviewService;

  @PostMapping("/orders/{orderId}/users/{userId}/reviews")
  public ResponseEntity<Review> createReview(@PathVariable String orderId, @PathVariable String userId, @RequestBody CreateReviewRequestDto createReviewRequestDto) {

    // Set Parameter to DTO
    createReviewRequestDto.setOrderId(orderId);
    if (createReviewRequestDto.getReviewType().equals(ReviewTypeEnum.FOR_CONSUMER)) {
      createReviewRequestDto.setShopperId(userId);
    } else {
      createReviewRequestDto.setTravelerId(userId);
    }

    // Create Review
    Review review = reviewService.createReview(createReviewRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(review);
  }

  @GetMapping("/reviews/{reviewId}")
  public ResponseEntity<Review> getReviewById(@PathVariable String reviewId) {
    Review review = reviewService.getReviewById(reviewId);
    return ResponseEntity.status(HttpStatus.OK).body(review);
  }

}
