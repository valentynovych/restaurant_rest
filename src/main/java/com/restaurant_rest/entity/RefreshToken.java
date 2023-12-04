package com.restaurant_rest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.mapstruct.control.MappingControl;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "refreshToken")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User user;
    @NonNull
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant expiryDate;

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate.toString() +
                '}';
    }
}
