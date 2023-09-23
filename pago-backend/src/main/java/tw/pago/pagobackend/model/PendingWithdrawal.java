package tw.pago.pagobackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingWithdrawal {
    private String pendingWithdrawalId;
    private String userId;
    private Integer withdrawalAmount;
}
