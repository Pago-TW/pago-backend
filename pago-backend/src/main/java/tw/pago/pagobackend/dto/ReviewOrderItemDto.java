package tw.pago.pagobackend.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ReviewOrderItemDto {
  private String orderItemId;
  private String name;

}
