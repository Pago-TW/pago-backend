package tw.pago.pagobackend.controller;


import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.JwtAuthenticationResponseDto;
import tw.pago.pagobackend.dto.NewPasswordDto;
import tw.pago.pagobackend.dto.PasswordRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.PasswordResetToken;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.AuthService;

@RestController
@Validated
public class AuthController {


  @Autowired
  private AuthService authService;

  @PostMapping("/auth/login")
  public ResponseEntity<JwtAuthenticationResponseDto> authenticateUser(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {

    JwtAuthenticationResponseDto jwtAuthenticationResponseDto = authService.login(userLoginRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(jwtAuthenticationResponseDto);


  }


  @PostMapping("/auth/register")
  public ResponseEntity<?> userRegister(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {

    User user = authService.register(userRegisterRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(user);


  }

  @PostMapping("/auth/request-password-reset")
  public ResponseEntity<?> requestPasswordReset(@RequestBody @Valid PasswordRequestDto passwordRequestDto) {
// TODO 該 user 如果二次發起重設密碼請求，直接更新原資料，把上一次的 token 蓋掉，避免該用戶重複發起多次請求，信箱內有很多連結都可以成功重設密碼，只能讓最新連結可以重設密碼
    PasswordResetToken passwordResetToken = authService.requestPasswordReset(passwordRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(passwordResetToken);
  }

  @PostMapping("/auth/reset-password/{token}")
  public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody @Valid NewPasswordDto newPasswordDto) {

    newPasswordDto.setToken(token);
    authService.resetPassword(newPasswordDto);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/auth/fetch-email-from-token")
  public ResponseEntity<String> fetchEmailFromToken(@RequestParam String token) {

    String email = authService.fetchEmailFromToken(token);

    return ResponseEntity.status(HttpStatus.OK).body(email);
  }

}
