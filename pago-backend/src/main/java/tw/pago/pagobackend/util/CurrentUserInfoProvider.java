package tw.pago.pagobackend.util;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.AuthenticationFacade;
import tw.pago.pagobackend.security.model.UserPrincipal;
import tw.pago.pagobackend.service.UserService;

@Component
public class CurrentUserInfoProvider {

  private final UserService userService;
  private final AuthenticationFacade authenticationFacade;

  public CurrentUserInfoProvider(@Lazy UserService userService, AuthenticationFacade authenticationFacade) {
    this.userService = userService;
    this.authenticationFacade = authenticationFacade;
  }

  public String getCurrentLoginUserId() {
    UserPrincipal userPrincipal = (UserPrincipal) authenticationFacade.getAuthentication().getPrincipal();
    return userPrincipal.getId();
  }

  public User getCurrentLoginUser() {
    String userId = getCurrentLoginUserId();
    return userService.getUserById(userId);
  }
}
