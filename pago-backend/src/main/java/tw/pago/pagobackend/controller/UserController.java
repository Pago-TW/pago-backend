package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.UserService;

@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/users/register")
  public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {

    try {
      User user = userService.register(userRegisterRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(user);

    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new User());
    }

  }
  @PostMapping("/users/login")
  public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
    User user = userService.login(userLoginRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PostMapping()
  public ResponseEntity<?> googleLogin() {

    return null;
  }
}
