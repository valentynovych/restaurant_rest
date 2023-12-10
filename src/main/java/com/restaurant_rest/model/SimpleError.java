package com.restaurant_rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleError {
    @Schema(example = "Повідомлення помилки")
    private String error;
}
