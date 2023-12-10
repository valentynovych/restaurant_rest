package com.restaurant_rest.entity.enums;

public enum PaymentMethod {

    CASH("Готівка"),
    CARD("Карта");

    public final String label;

    PaymentMethod(String label) {
        this.label = label;
    }
}
