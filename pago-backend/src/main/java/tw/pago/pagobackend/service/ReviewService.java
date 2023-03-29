package tw.pago.pagobackend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.model.Review;

public interface ReviewService {

  Review createReview(List<MultipartFile> files, CreateReviewRequestDto createReviewRequestDto);

  Review getReviewById(String reviewId);

  List<Review> getReviewList(ListQueryParametersDto listQueryParametersDto);

  Integer countReview(ListQueryParametersDto listQueryParametersDto);


  ReviewRatingResultDto calculateAverageRating(String targetId, ReviewTypeEnum reviewTypeEnum);


}
