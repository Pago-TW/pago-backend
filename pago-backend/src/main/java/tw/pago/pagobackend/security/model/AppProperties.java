package tw.pago.pagobackend.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private final Auth auth = new Auth();
  private final OAuth2 oAuth2 = new OAuth2();

  @Getter
  @Setter
  public static class Auth {
    private String tokenSecret;
    private long tokenExpirationMsec;
  }

  @Getter
  @Setter
  public static final class OAuth2 {
    private List<String> authorizedRedirectUris = new ArrayList<>();

  }
}
