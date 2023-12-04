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
    private String addressName;
    private String city;
    private String street;
    private String building;
    private String apartment;
    private String entrance;
    private String doorCode;
    private String floor;
    private String comment;
    @ManyToOne
    private User user;
}
