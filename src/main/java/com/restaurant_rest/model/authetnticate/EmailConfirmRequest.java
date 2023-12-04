package com.restaurant_rest.model.authetnticate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailConfirmRequest {
    private String email;
    private String confirmCode;
    private String message;
}
