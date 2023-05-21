package tw.pago.pagobackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidGoogleIdTokenException extends RuntimeException {

  public InvalidGoogleIdTokenException(String message) {
   super(message);
  }

  public InvalidGoogleIdTokenException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
