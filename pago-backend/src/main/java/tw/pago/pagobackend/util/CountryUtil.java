package tw.pago.pagobackend.util;

import com.neovisionaries.i18n.CountryCode;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class CountryUtil {

  public static String getChineseCountryName(String countryCode) {
    Locale locale = new Locale("zh", countryCode);
    return locale.getDisplayCountry(Locale.CHINESE);
  }
  public static String getChineseCountryName(CountryCode countryCode) {
    Locale locale = new Locale("zh", countryCode.getAlpha2());
    return locale.getDisplayCountry(Locale.CHINESE);
  }

  public static String getEnglishCountryName(String countryCode) {
    CountryCode code = CountryCode.getByCode(countryCode);
    if (code != null) {
      Locale locale = new Locale("en", countryCode);
      return locale.getDisplayCountry(Locale.ENGLISH);
    }
    return null;
  }


  public static String getEnglishCountryName(CountryCode countryCode) {
    CountryCode code = CountryCode.getByCode(countryCode.name());
    if (code != null) {
      Locale locale = new Locale("en", countryCode.getAlpha2());
      return locale.getDisplayCountry(Locale.ENGLISH);
    }
    return null;
  }
}
