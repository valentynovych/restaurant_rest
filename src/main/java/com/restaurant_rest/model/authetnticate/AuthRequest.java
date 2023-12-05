package com.restaurant_rest.model.authetnticate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthRequest {

    @Schema(example = "user@gmail.com")
    @NotEmpty(message = "Not empty")
    @Email
    private String email;
}
