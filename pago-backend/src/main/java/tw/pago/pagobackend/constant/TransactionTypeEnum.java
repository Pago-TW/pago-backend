package tw.pago.pagobackend.constant;

public enum TransactionTypeEnum {
    WITHDRAW("提領"),
    INCOME("委託收入"),
    REFUND("委託退款"),
    FEE("手續費");

    private final String description;

    TransactionTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
