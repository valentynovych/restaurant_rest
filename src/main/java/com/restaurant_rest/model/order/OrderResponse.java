package com.restaurant_rest.model.order;

import com.restaurant_rest.entity.*;
import com.restaurant_rest.entity.enums.OrderStatus;
import com.restaurant_rest.model.user.UserProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
public class OrderResponse {

    @Schema(example = "1")
    private Long id;
    private Instant datetimeOfCreate;
    private UserProfileResponse user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Integer usedBonuses;
    private Integer accruedBonuses;
    private Integer deliveryTime;
    private String cutlery;
    private Set<OrderItemResponse> orderItems;
}
