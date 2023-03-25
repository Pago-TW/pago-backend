package tw.pago.pagobackend.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dto.ReviewCreatorDto;
import tw.pago.pagobackend.dto.ReviewOrderDto;
import tw.pago.pagobackend.dto.ReviewResponseDto;
import tw.pago.pagobackend.dto.ReviewTargetDto;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OrderService;
import tw.pago.pagobackend.service.UserService;

@Component
public class ReviewAssembler implements Assembler<Review, ReviewResponseDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Override
    public ReviewResponseDto assemble(Review review) {
      User creator = userService.getUserById(review.getCreatorId());
      User target = userService.getUserById(review.getTargetId());
      Order order = orderService.getOrderById(review.getOrderId());

      ReviewCreatorDto creatorDto = modelMapper.map(creator, ReviewCreatorDto.class);
      ReviewTargetDto targetDto = modelMapper.map(target, ReviewTargetDto.class);
      ReviewOrderDto orderDto = modelMapper.map(order, ReviewOrderDto.class);

      ReviewResponseDto reviewResponseDto = modelMapper.map(review, ReviewResponseDto.class);

      reviewResponseDto.setCreator(creatorDto);
      reviewResponseDto.setTarget(targetDto);
      reviewResponseDto.setOrder(orderDto);

      return reviewResponseDto;
    }
}
