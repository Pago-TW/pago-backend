package tw.pago.pagobackend.service.impl;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.security.oauth2.CustomOAuth2User;

@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    if (oAuth2User instanceof OidcUser) {
      return new CustomOAuth2User((OidcUser) oAuth2User);
    }
    return oAuth2User;
  }
}
