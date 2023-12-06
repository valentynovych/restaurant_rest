package com.restaurant_rest.entity;


import com.restaurant_rest.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant datetimeOfCreate;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    @JoinTable(name = "orders_promotions",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id"))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Promotion> usedPromotion;
    private Integer usedBonuses;
    private Integer accruedBonuses;
    private String payment;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Address address;
    private Integer deliveryTime;
    private String reservedTime;
    private String comment;
    private String cutlery;
    @ManyToOne(fetch = FetchType.LAZY)
    private Staff orderPlaced;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems;
}
