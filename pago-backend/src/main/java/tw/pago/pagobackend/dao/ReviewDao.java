package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.Review;

public interface ReviewDao {

  void createReview(CreateReviewRequestDto createReviewRequestDto);

  Review getReviewById(String reviewId);

  List<Review> getReviewList(ListQueryParametersDto listQueryParametersDto);

  Integer countReview(ListQueryParametersDto listQueryParametersDto);

}
