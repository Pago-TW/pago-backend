package tw.pago.pagobackend.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import java.util.Date;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.model.AppProperties;
import tw.pago.pagobackend.security.model.UserPrincipal;

@Component
public class JwtTokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  private AppProperties appProperties;

  @Autowired
  private UserDao userDao;


  public JwtTokenProvider (AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  public String generateJwtToken(Authentication authentication) {

    Object userPrincipal = authentication.getPrincipal();
    String userId;

    if (userPrincipal instanceof UserPrincipal) {
      userId = ((UserPrincipal) userPrincipal).getId();
    } else if (userPrincipal instanceof DefaultOidcUser) {
      User user = userDao.getUserByEmail(((DefaultOidcUser) userPrincipal).getEmail().toString());
      userId = user.getUserId();
    } else if (userPrincipal instanceof DefaultOAuth2User) {
      User user = userDao.getUserByEmail(((DefaultOAuth2User) userPrincipal).getAttribute("email").toString());
      userId = user.getUserId();
    } else {
      throw new IllegalArgumentException("Unsupported principal type");
    }

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
        .compact();
  }

  public String getUserIdFromJwtToken(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(appProperties.getAuth().getTokenSecret())
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException ex) {
      logger.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty.");
    }
    return false;
  }
}
