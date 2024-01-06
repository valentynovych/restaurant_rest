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
    @Pattern(regexp = "^380(50|66|95|99|67|68|96|97|98|63|93|73)[0-9]{7}", message = "Phone not valid to pattern 380 XX xxx xx xx")
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
    @Pattern(regexp = "^[a-zA-Z0-9+._-]+@([a-zA-z]{2,10}\\.)+[a-zA-z]{2,5}$", message = "Email not valid pattern example@gmail.com")
    private String email;
    @NotEmpty(message = "Field is required")
    @Pattern(regexp = "((?=.*[0-9])(?=.*[A-Z])(?=.*[,./?])).{8,200}", message = "Minimum 8 symbol")
    @Schema(example = "A1!qwerty3")
    private String passwordToChange;
}
