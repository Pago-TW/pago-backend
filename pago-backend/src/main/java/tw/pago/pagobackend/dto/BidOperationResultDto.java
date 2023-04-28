package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.model.Bid;

@Data
@NoArgsConstructor
public class BidOperationResultDto {
  private Bid bid;
  private boolean isCreated;
}
