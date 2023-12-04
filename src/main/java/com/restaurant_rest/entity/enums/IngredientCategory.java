package com.restaurant_rest.entity.enums;

public enum IngredientCategory {
    MEATS("М'ясо"),
    CHEESES("Сири"),
    VEGETABLES("Овочі"),
    SEAFOOD("Морепродукти"),
    SAUCES("Соуси"),
    OTHER("Інше");

    public final String label;
    IngredientCategory(String label) {
        this.label = label;
    }
}
