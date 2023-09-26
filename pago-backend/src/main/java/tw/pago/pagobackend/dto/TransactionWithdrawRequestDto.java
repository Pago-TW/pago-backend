package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionWithdrawRequestDto {
    private Integer withdrawalAmount;
    private String bankAccountId;
    private String otpCode;
}
