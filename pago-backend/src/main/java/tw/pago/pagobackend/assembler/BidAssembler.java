package tw.pago.pagobackend.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dto.BidCreatorDto;
import tw.pago.pagobackend.dto.BidCreatorReviewDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.BidTripDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;

@Component
public class BidAssembler implements Assembler<Bid, BidResponseDto> {

  @Autowired
  private ModelMapper modelMapper;


  @Override
  @Deprecated
  public BidResponseDto assemble(Bid bid) {

//    // Get related data
//    Trip bidRelatedTrip = tripService.getTripById(bid.getTripId());
//    User creator = userService.getUserById(bidRelatedTrip.getShopperId());
//
//    // Get averageRating & totalReview
//    ReviewRatingResultDto reviewRatingResultDto = reviewService.calculateAverageRating(creator.getUserId(), ReviewTypeEnum.FOR_SHOPPER);
//    double averageRating = reviewRatingResultDto.getAverageRating();
//    int totalReview = reviewRatingResultDto.getTotalReviews();
//
//    // Set value to bidCreatorReviewDto
//    BidCreatorReviewDto bidCreatorReviewDto = new BidCreatorReviewDto();
//    bidCreatorReviewDto.setAverageRating(averageRating);
//    bidCreatorReviewDto.setTotalReview(totalReview);
//    bidCreatorReviewDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);
//
//
//
//    // Covert all data to DTO
//    BidCreatorDto bidCreatorDto = modelMapper.map(creator, BidCreatorDto.class);
//    BidTripDto bidTripDto = modelMapper.map(bidRelatedTrip, BidTripDto.class);
//    BidResponseDto bidResponseDto = modelMapper.map(bid, BidResponseDto.class);
//
//    // Set related data to ResponseDTO
//    bidResponseDto.setCreator(bidCreatorDto);
//    bidResponseDto.setTrip(bidTripDto);
//    bidResponseDto.getCreator().setReview(bidCreatorReviewDto);


//    return bidResponseDto;
    return null;
  }


  public BidResponseDto assemble(Bid bid, Trip bidRelatedTrip, User creator, BidCreatorReviewDto bidCreatorReviewDto) {

    // Covert all data to DTO
    BidCreatorDto bidCreatorDto = modelMapper.map(creator, BidCreatorDto.class);
    BidTripDto bidTripDto = modelMapper.map(bidRelatedTrip, BidTripDto.class);
    BidResponseDto bidResponseDto = modelMapper.map(bid, BidResponseDto.class);

    // Set related data to ResponseDTO
    bidResponseDto.setCreator(bidCreatorDto);
    bidResponseDto.getCreator().setReview(bidCreatorReviewDto);
    bidResponseDto.setTrip(bidTripDto);

    return bidResponseDto;
  }

}
