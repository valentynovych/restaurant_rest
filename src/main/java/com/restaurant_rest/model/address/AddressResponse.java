package com.restaurant_rest.model.address;

import lombok.Data;

@Data
public class AddressResponse {

    private Long id;
    private String addressName;
    private String city;
    private String street;
    private String building;
    private String apartment;
    private String entrance;
    private String doorCode;
    private String floor;
    private String comment;
}
