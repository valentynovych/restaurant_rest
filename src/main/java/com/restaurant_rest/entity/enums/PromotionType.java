package com.restaurant_rest.entity.enums;

public enum PromotionType {
    FOR_PRODUCT("Для одного товару"),
    FOR_CATEGORY("Для категорії товарів");
    public final String label;

    PromotionType(String label) {
        this.label = label;
    }
}
