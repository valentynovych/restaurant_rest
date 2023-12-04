package com.restaurant_rest.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfile {
    private Long userId;
    private String fullName;
    private String phone;
    private Date dateOfBirth;
    private String facebookUsername;
    private String email;
    private Integer amountOfOrders;
    private Integer activeBonuses;
}
