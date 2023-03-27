package tw.pago.pagobackend.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDto {
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
