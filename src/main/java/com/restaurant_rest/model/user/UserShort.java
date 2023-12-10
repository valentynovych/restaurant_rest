package com.restaurant_rest.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserShort {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "user@gmail.com")
    private String email;
}
