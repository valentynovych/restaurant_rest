package com.restaurant_rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class UserProfileResponse {
    @Schema(example = "1")
    private Long userId;
    @Schema(example = "Прізвище І'мя")
    private String fullName;
    @Schema(example = "380686868688")
    private String phone;
    private Date dateOfBirth;
    @Schema(example = "username")
    private String facebookUsername;
    @Schema(example = "user@gmail.com")
    private String email;
    @Schema(example = "20")
    private Integer amountOfOrders;
    @Schema(example = "250")
    private Integer activeBonuses;
    @Schema(example = "A1s#2sdvH")
    private String password;
}
