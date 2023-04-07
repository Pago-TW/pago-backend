package tw.pago.pagobackend.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.GoogleLoginRequestDto;
import tw.pago.pagobackend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import tw.pago.pagobackend.util.UuidGenerator;

@RestController
public class GoogleOAuthController {

  @Autowired
  private UuidGenerator uuidGenerator;
  @Autowired
  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

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

    String googleAuthorizationUrl = String.format("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=%s&redirect_uri=%s&scope=email%%20profile%%20openid&", clientId, encodedRedirectUri);
    return ResponseEntity.status(HttpStatus.OK).body(googleAuthorizationUrl);
  }


  @PostMapping("/oauth2/google-login")
  public ResponseEntity<?> handleGoogleLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody
      GoogleLoginRequestDto googleLoginRequestDto) {

    String idToken = googleLoginRequestDto.getIdToken();

    Authentication authentication = oAuth2AuthenticationSuccessHandler.processGoogleLogin(idToken, request, response);

    try {
      oAuth2AuthenticationSuccessHandler.handleGoogleLogin(request, response, authentication);
    } catch (IOException | ServletException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during authentication");
    }


    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
