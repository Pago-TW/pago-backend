package tw.pago.pagobackend.constant;

public enum AccountStatusEnum {
    ACTIVE("啟用"),
    DEACTIVE("停用"),
    WARNING("警告");

    private final String description;

    AccountStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
