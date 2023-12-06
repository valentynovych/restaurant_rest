package com.restaurant_rest.model.order;

import com.restaurant_rest.entity.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class OrderResponse {
    private Long id;
    private Instant datetimeOfCreate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Integer usedBonuses;
    private Integer accruedBonuses;
    private String payment;
}
