package tw.pago.pagobackend.util;

import tw.pago.pagobackend.constant.CurrencyEnum;

public class CurrencyUtil {
  public static int getDecimalScale(CurrencyEnum currency) {
    switch (currency) {
      case USD:
        return 2;
      default:
        return 0;
    }
  }

}
