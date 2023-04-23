package tw.pago.pagobackend.constant;

public enum CancelReasonCategoryEnum {
    OUT_OF_STOCK("缺貨"),
    FORCE_MAJEURE("不可抗力"),
    PERSONAL_FACTOR("個人因素"),
    OTHER("其他");

    private final String description;

    CancelReasonCategoryEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
