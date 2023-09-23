package tw.pago.pagobackend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecordListResponseDto {
  private int year;
  private int month;
  private List<TransactionRecordResponseDto> transactions;
}
