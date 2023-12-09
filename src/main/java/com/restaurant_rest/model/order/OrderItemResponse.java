package com.restaurant_rest.model.order;

import com.restaurant_rest.model.product.ProductShort;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemResponse {

    private Long id;
    private ProductShort product;
    private BigDecimal itemPrice;
    private BigDecimal itemSalePrice;
    private List<ProductShort> exclusionIngredients;
    private List<ProductShort> additionalIngredients;
    private boolean isGiftProduct;
}
