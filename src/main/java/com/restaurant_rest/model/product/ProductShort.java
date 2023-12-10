package com.restaurant_rest.model.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductShort {

    @Schema(example = "1")
    @NotNull(message = "Обов'язкове поле")
    @Min(value = 1, message = "Id не може бути менше {value}")
    @Max(value = Long.MAX_VALUE, message = "Значення не більше {value}")
    private Long id;
    @Schema(example = "Піца 'Пеппероні'")
    @NotNull(message = "Обов'язкове поле")
    @Size(max = 100, message = "Довжина поля не більше {max} символів")
    private String name;
}
