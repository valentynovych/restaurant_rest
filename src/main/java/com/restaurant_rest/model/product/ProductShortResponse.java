package com.restaurant_rest.model.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductShortResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "Піца ''Пеппероні''")
    private String name;
    @Schema(example = "true")
    private Boolean isActive;
    @Schema(example = "230")
    private BigDecimal price;
    @Schema(example = "8b5a2d3f-a50f-4113-9325-5828ae50828b_photo_2023-02-08_13-19-18.jpg")
    private String photo;
}
