package com.restaurant_rest.service;

import com.restaurant_rest.entity.User;
import com.restaurant_rest.mapper.UserMapper;
import com.restaurant_rest.model.UserProfile;
import com.restaurant_rest.repositoty.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final UserDetailsServiceImpl userDetailsService;

    public UserProfile getUserById(Long userId) {
        Optional<User> byId = userRepo.findById(userId);
        if (byId.isPresent()) {
            UserProfile userProfile = UserMapper.MAPPER.userToUserProfile(byId.get());
            return userProfile;
        } else {
            throw new EntityNotFoundException(String.format("Користувача з id: %s не знайдено", userId));
        }
    }

    public User getUserByEmail(String email) {
        User user;
        if (email != null && !email.isEmpty()) {
            Optional<User> byEmail = userRepo.findByEmail(email);
            if (byEmail.isPresent()) {
                user = byEmail.get();
                return user;
            }
        } else {
            throw new NullPointerException("Email not must be null");
        }
        return null;
    }

    public void saveUser(User userByEmail) {
        userRepo.save(userByEmail);
    }
}
