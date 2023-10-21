package tw.pago.pagobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.OrderResponseDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.exception.BadRequestException;
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
                total = tripService.countTrip(listQueryParametersDto);
                ListResponseDto<TripResponseDto> tripResponseDtoListResponseDto = ListResponseDto
                        .<TripResponseDto>builder()
                        .total(total)
                        .startIndex(listQueryParametersDto.getStartIndex())
                        .size(listQueryParametersDto.getSize())
                        .data(tripResponseDtoList)
                        .build();

                return tripResponseDtoListResponseDto;

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
