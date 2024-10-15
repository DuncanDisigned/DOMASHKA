package enam;

public enum FormField {
    USERNAME("username"),
    EMAIL("email"),
    PASSWORD("password"),
    CONFIRM_PASSWORD("confirm_password"),
    BIRTHDATE("birthdate"),
    LANGUAGE_LEVEL("language_level");

    private final String fieldName;

    FormField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}