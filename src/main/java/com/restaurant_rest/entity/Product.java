package com.restaurant_rest.entity;


import com.restaurant_rest.entity.enums.IngredientCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 200)
    private String name;
    private Boolean isActive;
    @Column(nullable = false)
    private Boolean isIngredient;
    private BigDecimal price;
    private Integer weight;
    @Column(length = 200)
    private String photo;
    private Boolean isNovelty;
    @Column(length = 1000)
    private String characteristics;
    @Column(length = 1000)
    private String description;
    private Boolean promotionIsActive;
    @ManyToOne
    private Promotion promotion;
    @ManyToOne
    private MainCategory mainCategory;
    @ManyToOne
    private Subcategory subcategory;
    @JoinTable(name = "products_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    @ManyToMany
    private List<Product> consistsOfIngredients;
    @JoinTable(name = "ingredients_main_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "main_category_id"))
    @ManyToMany
    private List<MainCategory> forMainCategory;
    @Enumerated(EnumType.STRING)
    private IngredientCategory ingredientCategory;
}
