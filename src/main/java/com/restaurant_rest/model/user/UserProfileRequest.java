package com.restaurant_rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserProfileRequest {
    @NotEmpty(message = "Field is required")
    @Size(max = 50, message = "Field length not more 50 symbol")
    @Schema(example = "Петро Окунський")
    private String fullName;
    @NotEmpty(message = "Field is required")
    @Size(max = 50, message = "Field length not more 50 symbol")
    @Pattern(regexp = "^(380)\\d{8,15}", message = "Phone not valid to pattern 380 XX xxx xx xx")
    @Schema(example = "380501212345")
    private String phone;
    @NotNull(message = "Field is required")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Schema(example = "23.10.2002")
    private Date dateOfBirth;
    @Size(max = 100, message = "Max length 100 symbol")
    @Schema(example = "facebookusername")
    private String facebookUsername;
    @NotEmpty(message = "Field is required")
    @Schema(example = "user@gmail.com")
    private String email;
    @NotEmpty(message = "Field is required")
    @Pattern(regexp = "([0-9]+[A-Z]+[!/.,?@]+[A-Za-z0-9]*){8,200}", message = "Minimum 8 symbol")
    @Schema(example = "A1!qwerty3")
    private String password;
}
