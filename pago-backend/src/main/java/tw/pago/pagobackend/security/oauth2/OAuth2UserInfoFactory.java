package tw.pago.pagobackend.security.oauth2;

import java.util.Map;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.exception.OAuth2AuthenticationProcessingException;

public class OAuth2UserInfoFactory {

  public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
    if(registrationId.equalsIgnoreCase(UserAuthProviderEnum.GOOGLE.toString())) {
      return new GoogleOAuth2UserInfo(attributes);
    } else {
      throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
    }
  }
}
