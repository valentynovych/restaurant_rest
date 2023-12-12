package com.restaurant_rest.service;

import com.restaurant_rest.entity.User;
import com.restaurant_rest.repositoty.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@mail.com");
        user.setConfirmEmail("1234");
        userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        "",
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                );
    }

    @Test
    void loadUserByUsername_ifUserIsPresent() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserDetails userDetails1 = userDetailsService.loadUserByUsername(user.getEmail());
        assertNotNull(userDetails1);
        assertEquals(userDetails.getUsername(), userDetails1.getUsername());
        assertEquals(userDetails.getAuthorities(), userDetails1.getAuthorities());
    }

    @Test
    void loadUserByUsername_ifUserIsEmpty() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(user.getEmail()));
    }
}