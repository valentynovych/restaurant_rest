package com.restaurant_rest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shopping_cart_item")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Product product;
    @Column(nullable = false)
    private BigDecimal itemPrice;
    private BigDecimal itemSalePrice;
    @ManyToMany
    @JoinTable(name = "cart_item_exclusion_ingredients",
            joinColumns = @JoinColumn(name = "cart_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> exclusionIngredients;
    @ManyToMany
    @JoinTable(name = "cart_item_additional_ingredients",
            joinColumns = @JoinColumn(name = "cart_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> additionalIngredients;
    private boolean isGiftProduct;
}
