package tw.pago.pagobackend.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dto.BidCreatorDto;
import tw.pago.pagobackend.dto.BidResponseDto;
import tw.pago.pagobackend.dto.BidTripDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.service.UserService;

@Component
public class BidAssembler implements Assembler<Bid, BidResponseDto> {

  @Autowired
  ModelMapper modelMapper;
  @Autowired
  UserService userService;
  @Autowired
  TripService tripService;


  @Override
  public BidResponseDto assemble(Bid bid) {

    // Get related data
    Trip trip = tripService.getTripById(bid.getTripId());
    User creator = userService.getUserById(trip.getShopperId());


    // Covert all data to DTO
    BidCreatorDto bidCreatorDto = modelMapper.map(creator, BidCreatorDto.class);
    BidTripDto bidTripDto = modelMapper.map(trip, BidTripDto.class);
    BidResponseDto bidResponseDto = modelMapper.map(bid, BidResponseDto.class);

    // Set related data to ResponseDTO
    bidResponseDto.setCreator(bidCreatorDto);
    bidResponseDto.setTrip(bidTripDto);


    return bidResponseDto;
  }
}
