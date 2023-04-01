package tw.pago.pagobackend.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import tw.pago.pagobackend.util.UuidGenerator;

@RestController
public class GoogleOAuthController {

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;



  private final ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  public GoogleOAuthController(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }


  @Autowired
  private UuidGenerator uuidGenerator;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @GetMapping("/oauth2/google-url")
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
  public ResponseEntity<String> googleLogin(@RequestBody  Map<String, String> requestBody) throws URISyntaxException {
    String redirectUri = requestBody.get("redirectUri");


    redirectUri = redirectUri.replace("http://localhost:3000", "http://localhost:8080/api/v1");

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(new URI(redirectUri));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }




}
