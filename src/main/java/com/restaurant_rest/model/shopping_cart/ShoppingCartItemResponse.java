package com.restaurant_rest.model.shopping_cart;

import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.user.UserShort;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShoppingCartItemResponse {
    private Long id;
    private UserShort user;
    private ProductShort product;
    private BigDecimal itemPrice;
    private BigDecimal itemSalePrice;
    private List<ProductShort> exclusionIngredients;
    private List<ProductShort> additionalIngredients;
    private boolean isGiftProduct;
}
