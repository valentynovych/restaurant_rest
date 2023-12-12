package com.restaurant_rest.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String addressName;
    @Column(nullable = false, length = 50)
    private String city;
    @Column(nullable = false, length = 100)
    private String street;
    @Column(nullable = false, length = 10)
    private String building;
    @Column(length = 10)
    private String apartment;
    @Column(length = 10)
    private String entrance;
    @Column(length = 10)
    private String doorCode;
    @Column(length = 10)
    private String floor;
    @Column(length = 200)
    private String comment;
    @ManyToOne
    private User user;
}
