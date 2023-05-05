package tw.pago.pagobackend.constant;

public enum GenderEnum {
    MALE("男性"),
    FEMALE("女性"),
    OTHER("其他"),
    PREFER_NOT_TO_SAY("不願透露");

    private final String description;

    GenderEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
