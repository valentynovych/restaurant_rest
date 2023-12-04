package com.restaurant_rest.entity;


import com.restaurant_rest.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "staff")
public class Staff
//        implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role staffRole;
    @Column(length = 100, nullable = false)
    private String password;
    @Column(length = 50, nullable = false)
    private String firstName;
    @Column(length = 50, nullable = false)
    private String lastName;
    @Column(length = 200, nullable = false)
    private String email;
    @Column(nullable = false)
    private Boolean status;
    @Column(length = 15, unique = true)
    private String phone;
    private Date dateOfBirth;
    @Column(length = 100)
    private String photo;
    @OneToMany(mappedBy = "orderPlaced", fetch = FetchType.LAZY)
    private List<Order> placedOrders;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(staffRole.name()));
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return status;
//    }
}
