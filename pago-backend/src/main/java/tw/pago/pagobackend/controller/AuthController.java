package tw.pago.pagobackend.controller;


import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.JwtAuthenticationResponseDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.AuthService;

@RestController
@Validated
public class AuthController {


  @Autowired
  private AuthService authService;

  @PostMapping("/auth/login") // TODO 登入後要更新 last_login
  public ResponseEntity<JwtAuthenticationResponseDto> authenticateUser(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {

    JwtAuthenticationResponseDto jwtAuthenticationResponseDto = authService.login(userLoginRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(jwtAuthenticationResponseDto);


  }


  @PostMapping("/auth/register")
  public ResponseEntity<?> userRegister(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {

    User user = authService.register(userRegisterRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(user);


  }
}
