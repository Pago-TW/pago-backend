package tw.pago.pagobackend.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDto {
  private String orderItemId;
  private String name;
  private String description;
  private Integer quantity;
  private BigDecimal unitPrice;
  @JsonProperty("purchaseCountryCode")
  private CountryCode purchaseCountry;
  private String purchaseCountryName;
  @JsonProperty("purchaseCityCode")
  private CityCode purchaseCity;
  private String purchaseCityName;
  private String purchaseDistrict;
  private String purchaseRoad;
  // private URL fileUrl;
  private List<URL> fileUrls;

}
