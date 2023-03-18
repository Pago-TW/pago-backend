package tw.pago.pagobackend.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.util.UuidGenerator;

@RestController
public class GoogleOAuthController {

  @Autowired
  private UuidGenerator uuidGenerator;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @GetMapping("/oauth/google-url")
  public ResponseEntity<String> getGoogleAuthorizationUrl() {
    String redirectUri = "http://localhost:8080/login/oauth2/code/google"; // also can be http://localhost:3000/google-callback
    String state = uuidGenerator.getUuid();

    String encodedRedirectUri;
    try {
      encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Error encoding redirect URI", e);
    }

    String googleAuthorizationUrl = String.format("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=%s&redirect_uri=%s&scope=email%%20profile%%20openid&state=%s", clientId, encodedRedirectUri, state);
    return ResponseEntity.status(HttpStatus.OK).body(googleAuthorizationUrl);
  }
}
