package com.restaurant_rest.model.shopping_cart;

import com.restaurant_rest.model.product.ProductShort;
import com.restaurant_rest.model.user.UserShort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShoppingCartItemResponse {
    @Schema(example = "1")
    private Long id;
    private UserShort user;
    private ProductShort product;
    @Schema(example = "150")
    private BigDecimal itemPrice;
    @Schema(example = "120")
    private BigDecimal itemSalePrice;
    private List<ProductShort> exclusionIngredients;
    private List<ProductShort> additionalIngredients;
    private boolean isGiftProduct;
}
