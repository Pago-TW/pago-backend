package tw.pago.pagobackend.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator {

  public UuidGenerator() {

  }

  public String getUuid() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
