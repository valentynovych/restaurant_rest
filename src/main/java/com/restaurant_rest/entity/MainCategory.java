package com.restaurant_rest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "main_category")
public class MainCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String categoryName;
    @Column(nullable = false)
    private Boolean isActive;
    @Column(length = 200)
    private String previewIcon;
    @Column(length = 5)
    private Integer topPosition;
    @Column(nullable = false, updatable = false)
    private Date dateOfCreate;
    @Column(length = 5)
    private Integer countChildProduct;
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Subcategory> subcategories;
    @OneToMany(mappedBy = "mainCategory")
    private List<Product> childProducts;
}
