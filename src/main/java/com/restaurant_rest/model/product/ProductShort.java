package com.restaurant_rest.model.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductShort {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "Піца 'Пеппероні'")
    private String name;
}
