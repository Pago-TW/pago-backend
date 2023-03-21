package tw.pago.pagobackend.service.impl;

import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.oauth2.CustomOAuth2User;
import tw.pago.pagobackend.security.oauth2.OAuth2UserInfo;
import tw.pago.pagobackend.security.oauth2.OAuth2UserInfoFactory;

@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

  @Autowired
  private UserDao userDao;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

    if (oAuth2User instanceof OidcUser) {
      return new CustomOAuth2User((OidcUser) oAuth2User);
    }
    return oAuth2User;
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
    if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
      throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
    }

    Optional<User> userOptional = Optional.ofNullable(
        userDao.getUserByEmail(oAuth2UserInfo.getEmail()));
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
      if (!user.getProvider().equals(UserAuthProviderEnum.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
        throw new OAuth2AuthenticationException("Looks like you're signed up with " +
            user.getProvider() + " account. Please use your " + user.getProvider() +
            " account to login.");
      }
      user = updateExistingUser(user, oAuth2UserInfo);
    } else {
      user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
    }

    return UserPrincipal.create(user, oAuth2UserInfo.getAttributes());
  }

  private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
    UserRegisterRequestDto userRegisterRequestDto = new UserRegisterRequestDto();

    userRegisterRequestDto.setProvider(UserAuthProviderEnum.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
    userRegisterRequestDto.setGoogleId(oAuth2UserInfo.getId());
    userRegisterRequestDto.setFirstName(oAuth2UserInfo.getName());
    userRegisterRequestDto.setEmail(oAuth2UserInfo.getEmail());
    userRegisterRequestDto.setAvatarUrl(oAuth2UserInfo.getImageUrl());
    userDao.createUser(userRegisterRequestDto);

    User user = userDao.getUserByEmail(userRegisterRequestDto.getEmail());
    return user;
  }


  private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
    existingUser.setFirstName(oAuth2UserInfo.getName());
    existingUser.setAvatarUrl(oAuth2UserInfo.getImageUrl());
    return userDao.updateUserInfo(existingUser);
  }
}
