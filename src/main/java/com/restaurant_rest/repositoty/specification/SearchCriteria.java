package com.restaurant_rest.repositoty.specification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
}

