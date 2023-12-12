package com.restaurant_rest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String subcategoryName;
    @ManyToOne
    private MainCategory parentCategory;
    @OneToMany(mappedBy = "subcategory")
    private List<Product> products;
}
