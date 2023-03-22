package tw.pago.pagobackend.security.oauth2;

import static tw.pago.pagobackend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.model.AppProperties;
import tw.pago.pagobackend.security.model.UserPrincipal;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CookieUtil;
import tw.pago.pagobackend.util.JwtTokenProvider;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String GOOGLE_ISS_URL = "https://accounts.google.com";

  private JwtTokenProvider jwtTokenProvider;
  private AppProperties appProperties;
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
  private UserDao userDao;
  private UuidGenerator uuidGenerator;

  @Autowired
  OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, AppProperties appProperties, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository, UserDao userDao, UuidGenerator uuidGenerator) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.appProperties = appProperties;
    this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    this.userDao = userDao;
    this.uuidGenerator = uuidGenerator;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {


    Map<String, Object> userInfoMap = getUserInfoFromAuthentication(authentication);

//    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//    String googleUserId = oAuth2User.getAttribute("sub");
//    String email = oAuth2User.getAttribute("email");
//    String firstName = oAuth2User.getAttribute("given_name");
//    String lastName = oAuth2User.getAttribute("family_name");
//    String avatarUrl = oAuth2User.getAttribute("picture");



    Optional<User> userOptional = Optional.ofNullable(userDao.getUserByEmail(
        (String) userInfoMap.get("email")));

    User user;
    if (userOptional.isPresent()) {

      // Get Old data
      User oldUser = userDao.getUserByEmail(userInfoMap.get("email").toString());

      // Set Data You want to update
      UpdateUserRequestDto updateUserRequestDto = UpdateUserRequestDto.builder()
          .email(userInfoMap.get("email").toString())
          .firstName(userInfoMap.get("firstName").toString())
          .lastName(userInfoMap.get("firstName").toString())
          .avatarUrl(userInfoMap.get("avatarUrl").toString())
          .build();

      // Compare with data you set, if null -> set oldUser data;
      updateUserRequestDto.fillEmptyFieldsWithOldData(oldUser);

      // update exist User
      userDao.updateUser(updateUserRequestDto);

    } else {
      String userId = uuidGenerator.getUuid();
      UserRegisterRequestDto userRegisterRequestDto = UserRegisterRequestDto.builder()
          .userId(userId)
          .account(userInfoMap.get("email").toString())
          .provider(UserAuthProviderEnum.valueOf(userInfoMap.get("provider").toString()))
          .googleId(userInfoMap.get("googleId").toString())
          .firstName(userInfoMap.get("firstName").toString())
          .lastName(userInfoMap.get("lastName").toString())
          .email(userInfoMap.get("email").toString())
          .avatarUrl(userInfoMap.get("avatarUrl").toString())
          .build();

      userDao.createUser(userRegisterRequestDto);
    }


    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    String jwtToken = jwtTokenProvider.generateJwtToken(authentication);

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", jwtToken)
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    return appProperties.getOAuth2().getAuthorizedRedirectUris()
        .stream()
        .anyMatch(authorizedRedirectUri -> {
          // Only validate host and port. Let the clients use different paths if they want to
          URI authorizedURI = URI.create(authorizedRedirectUri);
          if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
              && authorizedURI.getPort() == clientRedirectUri.getPort()) {
            return true;
          }
          return false;
        });
  }

  private User registerNewUser(UserPrincipal userPrincipal, OAuth2UserInfo oAuth2UserInfo) {
    UserRegisterRequestDto userRegisterRequestDto = UserRegisterRequestDto.builder()
        .provider(UserAuthProviderEnum.valueOf(userPrincipal.getProvider().toString()))
        .googleId(oAuth2UserInfo.getId())
        .firstName(oAuth2UserInfo.getName())
        .email(oAuth2UserInfo.getEmail())
        .avatarUrl(oAuth2UserInfo.getImageUrl())
        .build();

    userDao.createUser(userRegisterRequestDto);

    User user = userDao.getUserByEmail(userRegisterRequestDto.getEmail());
    return user;
  }

  private Map<String, Object> getUserInfoFromAuthentication(Authentication authentication) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    String googleId = oAuth2User.getAttribute("sub");
    String email = oAuth2User.getAttribute("email");
    String firstName = oAuth2User.getAttribute("given_name");
    String lastName = oAuth2User.getAttribute("family_name");
    String avatarUrl = oAuth2User.getAttribute("picture");

    String iss = "";
    if (oAuth2User.getAttribute("iss") != null) {
      iss = oAuth2User.getAttribute("iss").toString();
    }

    String provider;
    if (StringUtils.isNotBlank(iss) && iss.contains("google")) {
      provider =  UserAuthProviderEnum.GOOGLE.toString();
    } else {
      provider =  UserAuthProviderEnum.LOCAL.toString();
    }


    Map<String, Object> userInfoMap = new HashMap<>();
    userInfoMap.put("googleId", googleId);
    userInfoMap.put("email", email);
    userInfoMap.put("firstName", firstName);
    userInfoMap.put("lastName", lastName);
    userInfoMap.put("avatarUrl", avatarUrl);
    userInfoMap.put("provider", provider);


    return userInfoMap;
  }

}
