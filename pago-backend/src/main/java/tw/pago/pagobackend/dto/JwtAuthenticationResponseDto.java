package tw.pago.pagobackend.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtAuthenticationResponseDto {
  private JwtDto token;
  private UserDto user;



}
