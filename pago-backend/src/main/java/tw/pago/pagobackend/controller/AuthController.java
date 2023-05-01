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

  @PostMapping("/auth/login")
  public ResponseEntity<JwtAuthenticationResponseDto> authenticateUser(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {

    JwtAuthenticationResponseDto jwtAuthenticationResponseDto = authService.login(userLoginRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(jwtAuthenticationResponseDto);


  }


  @PostMapping("/auth/register") // TODO 註冊時，用正規表達式檢查手機格式、或是使用Java or Spring 附的 Annotation...檢查格式是否正確，例如：手機09開頭且10碼
  public ResponseEntity<?> userRegister(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) { // TODO 不允許手機、信箱在 user 資料表有重複出現的情況，必須在註冊的邏輯層檢查，做出錯誤處理，捕獲Exception 回傳409 or 400，除此之外資料表 user 也要添加唯一索引，做第二層防護！不能只做資料庫防護而已，因為資料庫有異常是拋 500，會讓前端難以處理，以及不好 debug，所以業務邏輯層是第一層防呆，最一開始就要撈到錯誤然後回傳正確的 StatusCode

    User user = authService.register(userRegisterRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(user);


  }
}
