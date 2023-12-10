package com.restaurant_rest.model.order;

import com.restaurant_rest.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusResponse {
    private OrderStatus status;
}
