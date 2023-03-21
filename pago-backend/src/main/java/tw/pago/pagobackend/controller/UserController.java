package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserLoginRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.UserService;

@Validated
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
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(User.builder().build());
    }

  }
  @PostMapping("/users/login")
  public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
    User user = userService.login(userLoginRequestDto);

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PatchMapping("/users/{userId}")
  public ResponseEntity<User> updateUser(@PathVariable String userId,
      @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {

    // Update User
    updateUserRequestDto.setUserId(userId);
    userService.updateUser(updateUserRequestDto);

    // Get User
    User user = userService.getUserById(updateUserRequestDto.getUserId());

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<User> getUserById(@PathVariable String userId) {
    User user = userService.getUserById(userId);

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PostMapping()
  public ResponseEntity<?> googleLogin() {

    return null;
  }
}
