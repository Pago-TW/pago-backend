package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.ReviewDao;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.util.UuidGenerator;


@Component
public class ReviewServiceImpl implements ReviewService {

  @Autowired
  private ReviewDao reviewDao;
  @Autowired
  private UuidGenerator uuidGenerator;

  @Override
  public Review createReview(CreateReviewRequestDto createReviewRequestDto) {
    String reviewId = uuidGenerator.getUuid();

    // Create Review
    createReviewRequestDto.setReviewId(reviewId);
    reviewDao.createReview(createReviewRequestDto);

    // Get Review and return
    Review review = reviewDao.getReviewById(reviewId);

    return review;
  }

  @Override
  public Review getReviewById(String reviewId) {
    Review review = reviewDao.getReviewById(reviewId);
    return review;
  }
}
