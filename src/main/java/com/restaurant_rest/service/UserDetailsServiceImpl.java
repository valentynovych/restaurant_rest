package com.restaurant_rest.service;

import com.restaurant_rest.entity.User;
import com.restaurant_rest.repositoty.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> byEmail = userRepo.findByEmail(username);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
         return new org.springframework.security.core.userdetails.User(
                 user.getEmail(),
                 "",
                 List.of(new SimpleGrantedAuthority("ROLE_USER")));
        } else {
            throw new UsernameNotFoundException(String.format("Email %s not found", username));
        }
    }
}
