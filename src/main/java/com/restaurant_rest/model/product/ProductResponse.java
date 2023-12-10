package com.restaurant_rest.model.product;

import com.restaurant_rest.entity.enums.IngredientCategory;
import com.restaurant_rest.model.category.MainCategoryShortResponse;
import com.restaurant_rest.model.category.SubcategoryShortResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "Піца")
    private String name;
    private Boolean isActive;
    @Schema(example = "120")
    private BigDecimal price;
    @Schema(example = "500")
    private Integer weight;
    @Schema(example = "filename.jpg")
    private String photo;
    private Boolean isNovelty;
    @Schema(example = "Характеристики товару ...")
    private String characteristics;
    @Schema(example = "Опис товару ...")
    private String description;
    private MainCategoryShortResponse mainCategory;
    private SubcategoryShortResponse subcategory;
    private List<ProductShortResponse> consistsOfIngredients;
    private List<MainCategoryShortResponse> forMainCategory;
    @Enumerated(EnumType.STRING)
    private IngredientCategory ingredientCategory;
}
