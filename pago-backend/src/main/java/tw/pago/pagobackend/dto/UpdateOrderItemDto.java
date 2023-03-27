package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UpdateOrderItemDto {

  private String orderItemId;

  private String name;

  private String description;

  private Integer quantity;

  private BigDecimal unitPrice;

  private String purchaseCountry;

  private String purchaseCity;

  private String purchaseDistrict;

  private String purchaseRoad;
}
