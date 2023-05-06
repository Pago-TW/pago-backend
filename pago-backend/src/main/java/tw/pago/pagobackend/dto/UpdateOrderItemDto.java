package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;


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


  private String purchaseRoad;
}
