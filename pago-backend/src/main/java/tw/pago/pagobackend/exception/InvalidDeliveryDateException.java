package tw.pago.pagobackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidDeliveryDateException extends RuntimeException {

  public InvalidDeliveryDateException(String message) {
    super(message);
  }
}
