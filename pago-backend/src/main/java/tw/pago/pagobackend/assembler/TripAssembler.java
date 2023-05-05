package tw.pago.pagobackend.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.model.Trip;

@Component
public class TripAssembler implements Assembler<Trip, TripResponseDto> {
  @Autowired
  private ModelMapper modelMapper;

  @Override
  public TripResponseDto assemble(Trip trip) {

    TripResponseDto tripResponseDto = modelMapper.map(trip, TripResponseDto.class);

    return tripResponseDto;
  }
}
