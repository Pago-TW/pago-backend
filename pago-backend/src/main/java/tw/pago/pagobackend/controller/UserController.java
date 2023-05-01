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
import tw.pago.pagobackend.dto.UserResponseDto;
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

  @PatchMapping("/users/{userId}") // TODO 更新時，用正規表達式檢查手機格式，或是使用Annotation,套件
                                   // 等等...檢查格式是否正確，例如：手機09開頭且10碼，若多處會用到這種格式檢查，建議可以考慮寫成 Util 拿來共用!
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
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId) {
    User user = userService.getUserById(userId);
    UserResponseDto userResponseDto = userService.getUserResponseDtoByUser(user);

    return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
  }

  @GetMapping("/user/me")
  @PreAuthorize("hasRole('ROLE_USER')")
  public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
    return userService.getUserById(userPrincipal.getId());
  }

}
