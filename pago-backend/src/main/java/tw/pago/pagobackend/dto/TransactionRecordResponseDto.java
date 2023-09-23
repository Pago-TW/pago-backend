package tw.pago.pagobackend.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecordResponseDto {
  private String transactionId;
  private String userId;
  private String transactionTitle;
  private String transactionDescription;
  private Integer transactionAmount;
  private String transactionType;
  private ZonedDateTime transactionDate;
  private String transactionStatus;
  private Detail detail;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Detail {
    private Integer balance;
    private String accountNumber;
    private String bankName;
    private String orderSerialNumber;
    private String orderName;
    private String cancelReason;
  }

}
