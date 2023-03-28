package tw.pago.pagobackend.model;

import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.CityCode;


@Getter
@Setter
@Builder
public class OrderItem {

  private String orderItemId;
  private String name;
  private String description;
  private Integer quantity;
  private BigDecimal unitPrice;
  private CountryCode purchaseCountry;
  private CityCode purchaseCity;
  private String purchaseDistrict;
  private String purchaseRoad;
  // private URL fileUrl;
  private List<URL> fileUrls;

  // For programing use
  private String purchaseCountryName;
  private String purchaseCityName;

}
