package com.restaurant_rest.model.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCriteria {

    private boolean isNovelty;
    private boolean isIngredients;
    private boolean isPromotional;
    private Long byCategoryId;
    private Long bySubcategoryId;
}
