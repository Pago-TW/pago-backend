package tw.pago.pagobackend.assembler;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dto.ReviewCreatorDto;
import tw.pago.pagobackend.dto.ReviewOrderDto;
import tw.pago.pagobackend.dto.ReviewResponseDto;
import tw.pago.pagobackend.dto.ReviewTargetDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;

@Component
@AllArgsConstructor
public class ReviewAssembler implements Assembler<Review, ReviewResponseDto> {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final OrderService orderService;
    private final TripService tripService;

    @Override
    public ReviewResponseDto assemble(Review review) {
      User creator = userService.getUserById(review.getCreatorId());
      User target = userService.getUserById(review.getTargetId());
      Order order = orderService.getOrderById(review.getOrderId());

      ReviewCreatorDto creatorDto = modelMapper.map(creator, ReviewCreatorDto.class);
      ReviewTargetDto targetDto = modelMapper.map(target, ReviewTargetDto.class);
      ReviewOrderDto orderDto = modelMapper.map(order, ReviewOrderDto.class);

      ReviewResponseDto reviewResponseDto = modelMapper.map(review, ReviewResponseDto.class);

      // TODO 效能瓶頸，重複查詢，例如：我有 10 筆 Review 都是 userA 創建的，所以我們其實只需要查詢一次資料庫知道 userA 是否正在旅途中，而不是因為有 10 筆 Review 都是 userA 創建，導致我每次查詢 review 都會多查一次userA 是否正在旅途中
      boolean isReviewCreatorTraveling = tripService.isUserTraveling(creator.getUserId());
      creatorDto.setTraveling(isReviewCreatorTraveling);

      reviewResponseDto.setCreator(creatorDto);
      reviewResponseDto.setTarget(targetDto);
      reviewResponseDto.setOrder(orderDto);

      return reviewResponseDto;
    }
}
