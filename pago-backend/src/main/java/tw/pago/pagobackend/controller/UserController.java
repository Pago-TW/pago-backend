package tw.pago.pagobackend.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserResponseDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.CurrentUser;
import tw.pago.pagobackend.security.model.UserPrincipal;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Validated
@RestController
@AllArgsConstructor
public class UserController {
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;
  private final CurrentUserInfoProvider currentUserInfoProvider;

  @Deprecated
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

  @PatchMapping("/users/me")
  public ResponseEntity<User> updateUser(@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
    String userId = currentUserInfoProvider.getCurrentLoginUserId();

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

  @GetMapping("/users/me")
  @PreAuthorize("hasRole('ROLE_USER')")
  public UserResponseDto getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {

    User user = userService.getUserById(userPrincipal.getId());
    UserResponseDto userResponseDto = userService.getUserResponseDtoByUser(user);
    return userResponseDto;
  }

  @PatchMapping("/users/me/avatar")
  public ResponseEntity<UserResponseDto> updateUserAvatar(@RequestParam("file") List<MultipartFile> files) {
    String userId = currentUserInfoProvider.getCurrentLoginUserId();

    // Update User Avatar
    userService.updateUserAvatar(userId, files);

    // After update successfully, get updated data then return
    User updatedUser = userService.getUserById(userId);
    UserResponseDto  updatedUserResponseDto = userService.getUserResponseDtoByUser(updatedUser);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUserResponseDto);
  }

}
