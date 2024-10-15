package enam;

public enum ResultField {
    USERNAME("Имя пользователя"),
    EMAIL("Электронная почта"),
    BIRTHDATE("Дата рождения"),
    LANGUAGE_LEVEL("Уровень языка");

    private final String displayName;

    ResultField(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}