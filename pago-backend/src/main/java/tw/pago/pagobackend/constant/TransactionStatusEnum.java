package tw.pago.pagobackend.constant;

public enum TransactionStatusEnum {
    WITHDRAWAL_IN_PROGRESS("提領中");

    private final String description;

    TransactionStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
