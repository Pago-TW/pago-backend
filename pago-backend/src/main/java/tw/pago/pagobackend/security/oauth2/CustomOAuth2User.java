package tw.pago.pagobackend.security.oauth2;

import java.net.URL;
import java.time.Instant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OidcUser, UserDetails {

  private OidcUser oidcUser;

  public CustomOAuth2User(OidcUser oidcUser) {
    this.oidcUser = oidcUser;
  }




  @Override
  public Map<String, Object> getAttributes() {
    return oidcUser.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oidcUser.getAuthorities();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return getName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return oidcUser.getAttribute("name");
  }


  public String getOpenId() {
    return oidcUser.getAttribute("openid");
  }

  // Add OidcUser methods
  @Override
  public Map<String, Object> getClaims() {
    return oidcUser.getClaims();
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return oidcUser.getUserInfo();
  }

  @Override
  public OidcIdToken getIdToken() {
    return oidcUser.getIdToken();
  }

  @Override
  public String getSubject() {
    return oidcUser.getSubject();
  }

  @Override
  public URL getIssuer() {
    return oidcUser.getIssuer();
  }

  @Override
  public String getGivenName() {
    return oidcUser.getGivenName();
  }

  @Override
  public String getFamilyName() {
    return oidcUser.getFamilyName();
  }

  @Override
  public String getMiddleName() {
    return oidcUser.getMiddleName();
  }

  // ... previous methods ...

  @Override
  public String getNickName() {
    return oidcUser.getNickName();
  }

  @Override
  public String getPreferredUsername() {
    return oidcUser.getPreferredUsername();
  }

  @Override
  public String getProfile() {
    return oidcUser.getProfile();
  }

  @Override
  public String getPicture() {
    return oidcUser.getPicture();
  }

  @Override
  public String getWebsite() {
    return oidcUser.getWebsite();
  }

  @Override
  public String getEmail() {
    return oidcUser.getEmail();
  }


  public Boolean getEmailVerified() {
    return oidcUser.getEmailVerified();
  }

  @Override
  public String getGender() {
    return oidcUser.getGender();
  }

  @Override
  public String getBirthdate() {
    return oidcUser.getBirthdate();
  }

  @Override
  public String getLocale() {
    return oidcUser.getLocale();
  }

  @Override
  public String getPhoneNumber() {
    return oidcUser.getPhoneNumber();
  }

  @Override
  public Boolean getPhoneNumberVerified() {
    return oidcUser.getPhoneNumberVerified();
  }

  @Override
  public AddressStandardClaim getAddress() {
    return oidcUser.getAddress();
  }

  @Override
  public Instant getUpdatedAt() {
    return oidcUser.getUpdatedAt();
  }
}

