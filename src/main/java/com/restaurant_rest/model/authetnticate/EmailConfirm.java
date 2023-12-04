package com.restaurant_rest.model.authetnticate;

import lombok.Data;

@Data
public class EmailConfirm {
    private String email;
    private String confirmCode;
}
