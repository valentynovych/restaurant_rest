package com.restaurant_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomError {

    private String objectName;
    private String field;
    private String defaultMessage;
    private Object rejectedValue;

//    public CustomError(String objectName, String field, String defaultMessage, Object rejectedValue) {
//        this(objectName, field, defaultMessage, rejectedValue);
//    }
}
