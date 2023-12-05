package com.restaurant_rest.model.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfileResponse {
    private Long userId;
    private String fullName;
    private String phone;
    private Date dateOfBirth;
    private String facebookUsername;
    private String email;
    private Integer amountOfOrders;
    private Integer activeBonuses;
    private String password;
}
