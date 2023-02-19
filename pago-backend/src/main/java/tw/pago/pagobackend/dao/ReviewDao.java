package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.model.Review;

public interface ReviewDao {

  void createReview(CreateReviewRequestDto createReviewRequestDto);

  Review getReviewById(String reviewId);

}
