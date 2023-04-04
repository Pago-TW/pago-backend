package tw.pago.pagobackend.dto;

import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.CityCode;


@Data
@NoArgsConstructor
public class UpdateOrderItemDto {

  private String orderItemId;

  private String name;

  private String description;

  private Integer quantity;

  private BigDecimal unitPrice;

  private CountryCode purchaseCountry;

  private CityCode purchaseCity;

  private String purchaseDistrict;

  private String purchaseRoad;
}
