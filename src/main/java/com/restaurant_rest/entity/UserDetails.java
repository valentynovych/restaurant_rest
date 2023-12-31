package com.restaurant_rest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "user_details")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    @Column(length = 15, unique = true)
    private String phone;
    @Column(nullable = false)
    private Date dateOfBirth;
    @Column(length = 200)
    private String photo;
    private Integer activeBonuses;
    private Integer usedBonuses;
    @Column(updatable = false, nullable = false)
    private Date registrationDate;
    @Column(length = 200)
    private String facebookUsername;
    @Column(length = 200)
    private String fullName;
    @Column(length = 200)
    private String passwordToChange;

}
