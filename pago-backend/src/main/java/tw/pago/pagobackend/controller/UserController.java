package tw.pago.pagobackend.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.CurrentUser;
import tw.pago.pagobackend.security.model.UserPrincipal;
import tw.pago.pagobackend.service.UserService;

@Validated
@RestController
public class UserController {
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;



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

  @GetMapping("/user/me")
  @PreAuthorize("hasRole('ROLE_USER')")
  public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
    return userService.getUserById(userPrincipal.getId());
  }

}
