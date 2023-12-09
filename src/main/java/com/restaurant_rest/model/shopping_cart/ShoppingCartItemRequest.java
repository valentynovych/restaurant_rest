package com.restaurant_rest.model.shopping_cart;

import com.restaurant_rest.model.product.ProductShort;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ShoppingCartItemRequest {
    @NotNull
    private ProductShort product;
    private List<ProductShort> exclusionIngredients;
    private List<ProductShort> additionalIngredients;
}
