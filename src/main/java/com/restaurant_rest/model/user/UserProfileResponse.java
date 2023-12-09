package com.restaurant_rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class UserProfileResponse {
    @Schema(example = "1")
    private Long userId;
    @Schema(example = "")
    private String fullName;
    @Schema(example = "")
    private String phone;
    @Schema()
    private Date dateOfBirth;
    @Schema
    private String facebookUsername;
    @Schema
    private String email;
    @Schema(example = "20")
    private Integer amountOfOrders;
    @Schema(example = "250")
    private Integer activeBonuses;
    @Schema
    private String password;
}
