package tw.pago.pagobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.SearchQueryParametersDto;
import tw.pago.pagobackend.dto.SearchResultDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.SearchService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final OrderService orderService;
    private final TripService tripService;
    private final UserService userService;

    public ListResponseDto<?> search(SearchQueryParametersDto searchQueryParametersDto) {
        String type = searchQueryParametersDto.getType();
        String query = searchQueryParametersDto.getQuery();
        Integer total = 0;
        switch (type.toLowerCase()) {
            case "order":
                List<Order> orderList = orderService.searchOrders(query);
  
                total = orderList.size();
                ListResponseDto<Order> orderListResponseDto = ListResponseDto.<Order>builder()
                .data(orderList)
                .startIndex(searchQueryParametersDto.getStartIndex())
                .size(searchQueryParametersDto.getSize())
                .total(total)
                .build();

                return orderListResponseDto;

            case "trip":
                List<Trip> tripList = tripService.searchTrips(query);
    
                total = tripList.size();
                ListResponseDto<Trip> tripListResponseDto = ListResponseDto.<Trip>builder()
                .data(tripList)
                .startIndex(searchQueryParametersDto.getStartIndex())
                .size(searchQueryParametersDto.getSize())
                .total(total)
                .build();

                return tripListResponseDto;

            case "user":
                List<User> userList = userService.searchUsers(query);
        
                total = userList.size();
                ListResponseDto<User> userListResponseDto = ListResponseDto.<User>builder()
                .data(userList)
                .startIndex(searchQueryParametersDto.getStartIndex())
                .size(searchQueryParametersDto.getSize())
                .total(total)
                .build();

                return userListResponseDto;

            default:
                throw new BadRequestException(type + " is not a valid search type");
        }
    }
}
