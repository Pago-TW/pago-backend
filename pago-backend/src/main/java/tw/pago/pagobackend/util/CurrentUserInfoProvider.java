package tw.pago.pagobackend.util;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
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
    Authentication authentication = authenticationFacade.getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      return userPrincipal.getId();
    }
    return null;
  }

  public User getCurrentLoginUser() {
    String userId = getCurrentLoginUserId();
    if (userId != null) {
      return userService.getUserById(userId);
    }
    return null;
  }
}
