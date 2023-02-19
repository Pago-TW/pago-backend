package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.model.Review;

public interface ReviewService {

  Review createReview(CreateReviewRequestDto createReviewRequestDto);

  Review getReviewById(String reviewId);

}
