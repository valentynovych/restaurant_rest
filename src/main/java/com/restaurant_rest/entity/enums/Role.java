package com.restaurant_rest.entity.enums;

public enum Role {

    ROLE_ADMIN("Головний адмін"),
    ROLE_MANAGER("Адміністратор"),
    ROLE_COURIER("Курьєр"),
    ROLE_ACCOUNTANT("Бухгалтер");

    public final String label;

    Role(String label) {
        this.label = label;
    }
}
