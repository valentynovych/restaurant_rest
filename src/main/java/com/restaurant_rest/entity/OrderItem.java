package com.restaurant_rest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant_rest.entity.enums.PromotionCondition;
import com.restaurant_rest.entity.enums.PromotionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;
    private BigDecimal itemPrice;
    private BigDecimal itemSalePrice;
    @ManyToMany
    @JoinTable(name = "order_item_exclusion_ingredients",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> exclusionIngredients;
    @ManyToMany
    @JoinTable(name = "order_item_additional_ingredients",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> additionalIngredients;
    private Boolean isGiftProduct;
    private String promotionName;
    @Enumerated(EnumType.STRING)
    private PromotionCondition promotionCondition;
    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;
    private Integer discountAmount;
    private Integer minimalAmount;
    private String promoCode;
}
