package com.restaurant_rest.model.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {
    @Schema(example = "1")
    @NotNull(message = "Обов'язкове поле")
    @Max(value = Long.MAX_VALUE, message = "Значення поля не більше {value} символів")
    private Long id;
    @Schema(example = "Мій дім")
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 100, message = "Довжина поля не більше {max} символів")
    private String addressName;
    @Schema(example = "Київ")
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 50, message = "Довжина поля не більше {max} символів")
    private String city;
    @Schema(example = "пр. Коцюбинського")
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 100, message = "Довжина поля не більше {max} символів")
    private String street;
    @Schema(example = "14")
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 10, message = "Довжина поля не більше {max} символів")
    private String building;
    @Schema(example = "105")
    @Size(max = 10, message = "Довжина поля не більше {max} символів")
    private String apartment;
    @Schema(example = "1 під'їзд")
    @Size(max = 10, message = "Довжина поля не більше {max} символів")
    private String entrance;
    @Schema(example = "1234")
    @Size(max = 10, message = "Довжина поля не більше {max} символів")
    private String doorCode;
    @Schema(example = "1")
    @Size(max = 10, message = "Довжина поля не більше {max} символів")
    private String floor;
    @Schema(example = "Коментар")
    @Size(max = 10, message = "Довжина поля не більше {max} символів")
    private String comment;
}
