package tw.pago.pagobackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@AllArgsConstructor
public class DailyCountController {
  private final CurrentUserInfoProvider currentUserInfoProvider;

}
