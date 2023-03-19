package tw.pago.pagobackend.security.oauth2;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.service.UserService;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final UserService userService;

  @Autowired
  public OAuth2AuthenticationSuccessHandler(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomOAuth2User oAuth2User = new CustomOAuth2User((OidcUser) authentication.getPrincipal());

    userService.processOAuth2PostLogin(oAuth2User.getEmail());
    response.sendRedirect("/index");


  }
}
