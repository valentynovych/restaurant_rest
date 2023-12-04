package com.restaurant_rest.model;

import lombok.Data;

@Data
public class EmailConfirm {
    private String email;
    private String confirmCode;
}
