package tw.pago.pagobackend.constant;

public enum TransactionStatusEnum {
    WITHDRAWAL_IN_PROGRESS("提領中"),
    WITHDRAWAL_SUCCESS("提領成功"),
    WITHDRAWAL_FAILED("提領失敗"),
    REFUND_IN_PROGRESS("退款中"),
    REFUND_SUCCESS("退款成功"),
    REFUND_FAILED("退款失敗"),
    COMPLETED("已完成")
    ;

    private final String description;

    TransactionStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
