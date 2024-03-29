package tw.pago.pagobackend.service.impl;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.assembler.ReviewAssembler;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dao.ReviewDao;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.service.FileService;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.UuidGenerator;


@Component
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private static final String OBJECT_TYPE = "review";

  private final ReviewDao reviewDao;
  private final UuidGenerator uuidGenerator;
  private final FileService fileService;
  private final OrderService orderService;
  private final CurrentUserInfoProvider currentUserInfoProvider;

  @Override
  public Review createReview(List<MultipartFile> files, CreateReviewRequestDto createReviewRequestDto) {
    String reviewId = uuidGenerator.getUuid();
    Order order = createReviewRequestDto.getOrder();
    String consumerId = order.getConsumerId();
    String shopperId = order.getShopper().getUserId();
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();


    // Determine the role of the currentLoginUser in the order and set the targetId and reviewType accordingly
    if (currentLoginUserId.equals(consumerId)) {
      createReviewRequestDto.setTargetId(shopperId);
      createReviewRequestDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);
    } else {
      createReviewRequestDto.setTargetId(consumerId);
      createReviewRequestDto.setReviewType(ReviewTypeEnum.FOR_CONSUMER);
    }

    // Create Review
    createReviewRequestDto.setReviewId(reviewId);
    reviewDao.createReview(createReviewRequestDto);

    // Upload file
    CreateFileRequestDto createFileRequestDto = new CreateFileRequestDto();
    createFileRequestDto.setFileCreator(createReviewRequestDto.getCreatorId());
    createFileRequestDto.setObjectId(reviewId);
    createFileRequestDto.setObjectType(OBJECT_TYPE);
    List<URL> uploadedUrls = fileService.uploadFile(files, createFileRequestDto);
    // print out all uploadedurls
    for (URL url: uploadedUrls) {
      System.out.println(url);
      System.out.println("Successfully uploaded!");
    }

    // Get Review and return
    Review review = getReviewById(reviewId);

    return review;
  }

  @Override
  public Review getReviewById(String reviewId) {
    Review review = reviewDao.getReviewById(reviewId);


    //get file url
    List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(reviewId, OBJECT_TYPE);
    review.setFileUrls(fileUrls);
    return review;
  }


  @Override
  public List<Review> getReviewList(ListQueryParametersDto listQueryParametersDto) {


    List<Review> reviewList = reviewDao.getReviewList(listQueryParametersDto);

    reviewList.forEach(review -> {
      List<URL> fileUrls = fileService.getFileUrlsByObjectIdnType(review.getReviewId(), OBJECT_TYPE);
      review.setFileUrls(fileUrls);
    });


    return reviewList;
  }

  @Override
  public Integer countReview(ListQueryParametersDto listQueryParametersDto) {

    Integer total = reviewDao.countReview(listQueryParametersDto);

    return total;
  }


  @Override
  public ReviewRatingResultDto calculateAverageRating(String targetId, ReviewTypeEnum reviewType) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .targetId(targetId)
        .reviewType(reviewType)
        .orderBy("create_date")
        .sort("DESC")
        .build();

    // Get review list for target user
    List<Review> targetUserReviewList = reviewDao.getReviewList(listQueryParametersDto);


    // Calculation
    double totalRating = 0;
    int numberOfReviews = targetUserReviewList.size();
    for (Review review : targetUserReviewList) {
      totalRating += review.getRating();
    }

    double averageRating = totalRating / numberOfReviews;
    double roundedAverageRating = Math.round(averageRating * 10) / 10.0;

    // Set result;
    ReviewRatingResultDto reviewRatingResultDto = new ReviewRatingResultDto();
    reviewRatingResultDto.setAverageRating(roundedAverageRating);
    reviewRatingResultDto.setTotalReviews(numberOfReviews);

    return reviewRatingResultDto;
  }
}
