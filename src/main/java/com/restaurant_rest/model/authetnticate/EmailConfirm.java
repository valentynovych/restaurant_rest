package com.restaurant_rest.model.authetnticate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailConfirm {
    @Schema(example = "user@gmaul.com")
    @NotEmpty(message = "Not empty")
    @Email
    private String email;
    @Schema(example = "1000")
    @NotEmpty(message = "Not empty")
    @Pattern(regexp = "\\d{4}")
    private String confirmCode;
}
