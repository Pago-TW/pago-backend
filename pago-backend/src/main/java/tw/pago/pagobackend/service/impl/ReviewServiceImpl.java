package tw.pago.pagobackend.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.assembler.ReviewAssembler;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dao.ReviewDao;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewResponseDto;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.UuidGenerator;


@Component
public class ReviewServiceImpl implements ReviewService {

  @Autowired
  private ReviewDao reviewDao;
  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private UserService userService;
  @Autowired
  private ReviewAssembler reviewAssembler;

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


  @Override
  public List<Review> getReviewList(ListQueryParametersDto listQueryParametersDto) {


    List<Review> reviewList = reviewDao.getReviewList(listQueryParametersDto);




    return reviewList;
  }

  @Override
  public Integer countReview(ListQueryParametersDto listQueryParametersDto) {

    Integer total = reviewDao.countReview(listQueryParametersDto);

    return total;
  }

  @Override
  public Integer calculateAverageRating(String userId, ReviewTypeEnum reviewTypeEnum) {


    return null;
  }
}
