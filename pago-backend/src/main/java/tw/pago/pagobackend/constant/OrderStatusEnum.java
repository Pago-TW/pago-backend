package tw.pago.pagobackend.constant;

public enum OrderStatusEnum {
    // 在信件說明好雙方權益
    REQUESTED("待確認"), // 1
    TO_BE_PURCHASED("待購買"), // 2
    TO_BE_DELIVERED("待面交"), // 3
    DELIVERED("已送達"), // 代購者按  // 4
    FINISHED("已完成"), // 委託者按 // 5
    CANCELED("已取消"),
    TO_BE_CANCELED("申請取消"),
    TO_BE_POSTPONED("申請延期");

    private final String description;

    OrderStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
