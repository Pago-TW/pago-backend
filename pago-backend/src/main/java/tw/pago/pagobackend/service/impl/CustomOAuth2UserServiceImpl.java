package tw.pago.pagobackend.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.exception.OAuth2AuthenticationProcessingException;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.model.UserPrincipal;
import tw.pago.pagobackend.security.oauth2.CustomOAuth2User;
import tw.pago.pagobackend.security.oauth2.OAuth2UserInfo;
import tw.pago.pagobackend.security.oauth2.OAuth2UserInfoFactory;
import tw.pago.pagobackend.service.UserService;

@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

  @Autowired
  private UserDao userDao;
  @Autowired
  private UserService userService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

    try {
      return processOAuth2User(oAuth2UserRequest, oAuth2User);
    } catch (AuthenticationException ex) {
      throw ex;
    } catch (Exception ex) {
      // Throwing an instance of AuthenticationException will trigger the
      // OAuth2AuthenticationFailureHandler
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
        .getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
    if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
      throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
    }

    Optional<User> userOptional = Optional.ofNullable(
        userDao.getUserByEmail(oAuth2UserInfo.getEmail()));
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
      if (!user.getProvider()
          .equals(UserAuthProviderEnum.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
        throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
            user.getProvider() + " account. Please use your " + user.getProvider() +
            " account to login.");
      }
      user = updateExistingUser(user, oAuth2UserInfo);
    } else {
      user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
    }

    UserPrincipal userPrincipal = UserPrincipal.create(user, oAuth2UserInfo.getAttributes());
    Map<String, Object> userAttributes = new HashMap<>(oAuth2User.getAttributes());
    userAttributes.put("userPrincipal", userPrincipal);
    return new DefaultOAuth2User(oAuth2User.getAuthorities(), userAttributes, "userPrincipal");

  }

  private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
    UserRegisterRequestDto userRegisterRequestDto = UserRegisterRequestDto.builder()
        .provider(UserAuthProviderEnum.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
        .googleId(oAuth2UserInfo.getId())
        .firstName(oAuth2UserInfo.getName())
        .email(oAuth2UserInfo.getEmail())
        .avatarUrl(oAuth2UserInfo.getImageUrl())
        .build();

    userDao.createUser(userRegisterRequestDto);

    User user = userDao.getUserByEmail(userRegisterRequestDto.getEmail());
    return user;
  }

  private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {

    UpdateUserRequestDto updateUserRequestDto = UpdateUserRequestDto.builder()
        .email(existingUser.getEmail())
        .firstName(oAuth2UserInfo.getName())
        .avatarUrl(oAuth2UserInfo.getImageUrl())
        .build();

    userService.updateUser(updateUserRequestDto);

    User updatedUser = userDao.getUserByEmail(existingUser.getEmail());
    return updatedUser;
  }
}
