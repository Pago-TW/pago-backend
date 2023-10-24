package tw.pago.pagobackend.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.ReviewRatingResultDto;
import tw.pago.pagobackend.dto.SearchTripResponseDto;
import tw.pago.pagobackend.dto.SearchTripUserDetailDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.ReviewService;
import tw.pago.pagobackend.service.SearchService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final OrderService orderService;
    private final TripService tripService;
    private final UserService userService;
    private final ReviewService reviewService;

    public ListResponseDto<?> search(ListQueryParametersDto listQueryParametersDto, String type) {

        Integer total = 0;
        switch (type.toLowerCase()) {
            case "order":
                List<OrderResponseDto> orderResponseDtoList = orderService
                        .getOrderResponseDtoList(listQueryParametersDto);
                total = orderService.countOrder(listQueryParametersDto);

                ListResponseDto<OrderResponseDto> orderListResponseDto = ListResponseDto.<OrderResponseDto>builder()
                        .total(total)
                        .startIndex(listQueryParametersDto.getStartIndex())
                        .size(listQueryParametersDto.getSize())
                        .data(orderResponseDtoList)
                        .build();

                return orderListResponseDto;

            case "trip":

                List<TripResponseDto> tripResponseDtoList;
                tripResponseDtoList = tripService.getTripResponseDtoList(listQueryParametersDto);

                // Initialize a list of searchTripResponseDto
                List<SearchTripResponseDto> searchTripResponseDtoList = new ArrayList<>();
                for (TripResponseDto tripResponseDto : tripResponseDtoList) {
                    SearchTripResponseDto searchTripResponseDto = new SearchTripResponseDto();

                    // Copy the properties from tripResponseDto to searchTripResponseDto
                    BeanUtils.copyProperties(tripResponseDto, searchTripResponseDto);

                    // Get additional user details
                    String userId = tripResponseDto.getShopperId();
                    User user = userService.getUserById(userId);
                    ReviewRatingResultDto reviewRatingResultDto = reviewService.calculateAverageRating(userId, ReviewTypeEnum.FOR_SHOPPER);
                    
                    // Map user details to searchTripUserDetailDto
                    SearchTripUserDetailDto searchTripUserDetailDto = new SearchTripUserDetailDto();
                    searchTripUserDetailDto.setAvatarUrl(user.getAvatarUrl());
                    searchTripUserDetailDto.setFullName(user.getFullName());
                    searchTripUserDetailDto.setAverageRating(reviewRatingResultDto.getAverageRating());
                    searchTripUserDetailDto.setTotalReview(reviewRatingResultDto.getTotalReviews());
                    searchTripUserDetailDto.setReviewType(ReviewTypeEnum.FOR_SHOPPER);
                    
                    searchTripResponseDto.setUserDetail(searchTripUserDetailDto);
                    searchTripResponseDtoList.add(searchTripResponseDto);
                }
           

                total = tripService.countTrip(listQueryParametersDto);
                ListResponseDto<SearchTripResponseDto> searchTripResponseDtoListResponseDto = ListResponseDto
                        .<SearchTripResponseDto>builder()
                        .total(total)
                        .startIndex(listQueryParametersDto.getStartIndex())
                        .size(listQueryParametersDto.getSize())
                        .data(searchTripResponseDtoList)
                        .build();

                return searchTripResponseDtoListResponseDto;

            case "user":
                List<User> userList = userService.searchUsers(listQueryParametersDto.getSearch());

                total = userList.size();
                ListResponseDto<User> userListResponseDto = ListResponseDto.<User>builder()
                        .data(userList)
                        .startIndex(listQueryParametersDto.getStartIndex())
                        .size(listQueryParametersDto.getSize())
                        .total(total)
                        .build();

                return userListResponseDto;

            default:
                throw new BadRequestException(type + " is not a valid search type");
        }
    }
}
