package tw.pago.pagobackend.security.oauth2;

import static tw.pago.pagobackend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.InvalidGoogleIdTokenException;
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
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String GOOGLE_CLIENT_ID;


  private JwtTokenProvider jwtTokenProvider;
  private AppProperties appProperties;
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
  private UserDao userDao;
  private UserService userService;
  private UuidGenerator uuidGenerator;


  @Autowired
  public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, AppProperties appProperties,
      HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
      UserDao userDao, UuidGenerator uuidGenerator,
      UserService userService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.appProperties = appProperties;
    this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    this.userDao = userDao;
    this.uuidGenerator = uuidGenerator;
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {


    Map<String, Object> userInfoMap = getUserInfoFromAuthentication(authentication);


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
          .lastLogin(new Date())
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
          .gender(GenderEnum.PREFER_NOT_TO_SAY)
          .accountStatus(AccountStatusEnum.ACTIVE)
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

  public String handleGoogleLogin(HttpServletRequest request, Authentication authentication) throws IOException, ServletException {

    Map<String, Object> userInfoMap = getUserInfoFromAuthentication(authentication);

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
          .lastName(userInfoMap.get("lastName").toString())
          .avatarUrl(userInfoMap.get("avatarUrl").toString())
          .lastLogin(new Date())
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
          .gender(GenderEnum.PREFER_NOT_TO_SAY)
          .provider(UserAuthProviderEnum.valueOf(userInfoMap.get("provider").toString()))
          .googleId(userInfoMap.get("googleId").toString())
          .accountStatus(AccountStatusEnum.ACTIVE)
          .firstName(userInfoMap.get("firstName").toString())
          .lastName(userInfoMap.get("lastName").toString())
          .email(userInfoMap.get("email").toString())
          .avatarUrl(userInfoMap.get("avatarUrl").toString())
          .build();

      userDao.createUser(userRegisterRequestDto);
    }

    String jwtToken = generateJwtToken(authentication);

    JsonObject json = new JsonObject();
    setJwtTokenToJsonObject(jwtToken, json);
    user = userService.getUserByEmail(userInfoMap.get("email").toString());
    setUserInfoToJsonObject(user, json);

    return json.toString();
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

  protected String generateJwtToken(Authentication authentication) {
    String jwtToken = jwtTokenProvider.generateJwtToken(authentication);
    return jwtToken;
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

  public Authentication processGoogleLogin(String idToken) {
    // 使用id_token和access_token進行後續的驗證和註冊過程
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
        .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
        .build();

    GoogleIdToken googleIdToken;
    try {
      googleIdToken = verifier.verify(idToken);
    } catch (GeneralSecurityException | IOException e) {
      throw new RuntimeException("無法驗證 Google ID Token", e);
    }

    if (googleIdToken == null) {
      throw new InvalidGoogleIdTokenException("無效的 Google ID Token");
    }

    Payload payload = googleIdToken.getPayload();

    Map<String, Object> userInfoMap = getUserInfoFromPayload(payload);

    // 使用驗證結果創建一個 Authentication 對象
    OAuth2User oAuth2User = new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        userInfoMap,
        "name"
    );

    Authentication authentication = new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "google");

    return authentication;
  }


  private Map<String, Object> getUserInfoFromPayload(Payload payload) {
    Map<String, Object> userInfoMap = new HashMap<>();

    userInfoMap.put("iss", payload.getIssuer());
    userInfoMap.put("azp", payload.getAuthorizedParty());
    userInfoMap.put("aud", payload.getAudience());
    userInfoMap.put("sub", payload.getSubject());
    userInfoMap.put("email", payload.getEmail());
    userInfoMap.put("email_verified", payload.getEmailVerified());
    userInfoMap.put("at_hash", (String) payload.get("at_hash"));
    userInfoMap.put("nonce", (String) payload.get("nonce"));
    userInfoMap.put("name", (String) payload.get("name"));
    userInfoMap.put("picture", (String) payload.get("picture"));
    userInfoMap.put("given_name", (String) payload.get("given_name"));
    userInfoMap.put("family_name", (String) payload.get("family_name"));
    userInfoMap.put("locale", (String) payload.get("locale"));
    userInfoMap.put("iat", payload.getIssuedAtTimeSeconds());
    userInfoMap.put("exp", payload.getExpirationTimeSeconds());

    return userInfoMap;
  }


  private JsonObject setJwtTokenToJsonObject(String jwtToken, JsonObject json) {
    // Create a token object
    JsonObject tokenObject = new JsonObject();
    tokenObject.addProperty("tokenType", "Bearer");
    tokenObject.addProperty("accessToken", jwtToken);
    json.add("token", tokenObject);


    return json;
  }

  private JsonObject setUserInfoToJsonObject(User user, JsonObject json) {
    JsonObject userObject = new JsonObject();
    userObject.addProperty("userId", user.getUserId());
    userObject.addProperty("email", user.getEmail());
    userObject.addProperty("account", user.getAccount());
    userObject.addProperty("firstName", user.getFirstName());
    userObject.addProperty("lastName", user.getLastName());
    userObject.addProperty("fullName", user.getFullName());
    userObject.addProperty("phone", user.getPhone());
    userObject.addProperty("provider", user.getProvider().toString());
    userObject.addProperty("avatarUrl", user.getAvatarUrl().toString());
    userObject.addProperty("createDate", user.getCreateDate().toString());
    userObject.addProperty("updateDate", user.getUpdateDate().toString());
    userObject.addProperty("lastLogin", user.getLastLogin().toString());
    userObject.addProperty("isPhoneVerified", user.getIsPhoneVerified());
    json.add("user", userObject);

    return json;
  }

}
