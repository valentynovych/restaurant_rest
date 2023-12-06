package com.restaurant_rest.model.product;

import com.restaurant_rest.entity.MainCategory;
import com.restaurant_rest.entity.Promotion;
import com.restaurant_rest.entity.Subcategory;
import com.restaurant_rest.entity.enums.IngredientCategory;
import com.restaurant_rest.model.product.ProductShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Boolean isActive;
    private BigDecimal price;
    private Integer weight;
    private String photo;
    private Boolean isNovelty;
    private String characteristics;
    private String description;
    private Boolean promotionIsActive;
    private Promotion promotion;
    private MainCategory mainCategory;
    private Subcategory subcategory;
    private List<ProductShortResponse> consistsOfIngredients;
    private List<MainCategory> forMainCategory;
    @Enumerated(EnumType.STRING)
    private IngredientCategory ingredientCategory;
}
