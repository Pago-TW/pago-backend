package tw.pago.pagobackend.dto;

import com.neovisionaries.i18n.CountryCode;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;


@Data
@NoArgsConstructor
public class CreateOrderItemDto {

  @NotNull
  private String orderItemId;

  @NotNull
  private String name;

  private String description;

  @NotNull
  private Integer quantity;

  @NotNull
  private BigDecimal unitPrice;

  @NotNull
  private CountryCode purchaseCountry;

  @NotNull
  private CityCode purchaseCity;

  @NotNull
  private String purchaseDistrict;

  @NotNull
  private String purchaseRoad;

}
