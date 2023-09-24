package tw.pago.pagobackend.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTabViewResponseDto {

  private Map<Integer, List<TransactionTabViewDto>> transactionTabViewMap;
}
