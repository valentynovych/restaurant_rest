package com.restaurant_rest.service;

import com.restaurant_rest.entity.User;
import com.restaurant_rest.mapper.UserMapper;
import com.restaurant_rest.model.user.UserProfileRequest;
import com.restaurant_rest.model.user.UserProfileResponse;
import com.restaurant_rest.repositoty.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepo userRepo;

    public UserProfileResponse getUserById(Long userId) {
        log.info("getUserById() -> start with id: " + userId);
        Optional<User> byId = userRepo.findById(userId);
        if (byId.isPresent()) {
            UserProfileResponse userProfile = UserMapper.MAPPER.userToUserProfile(byId.get());
            log.info("getUserById() -> user is present, return userProfile");
            return userProfile;
        } else {
            log.info("getUserById() -> user is empty, throw new EntityNotFoundException()");
            throw new EntityNotFoundException(String.format("Користувача з id: %s не знайдено", userId));
        }
    }

    public User getUserByEmail(String email) {
        log.info("getUserByEmail() -> start with email: " + email);
        User user;
        if (email != null && !email.isEmpty()) {
            Optional<User> byEmail = userRepo.findByEmail(email);
            if (byEmail.isPresent()) {
                user = byEmail.get();
                log.info("getUserByEmail() -> user is present, return user");
                return user;
            }
        } else {
            log.error("getUserByEmail() -> user is empty, throw new NullPointerException()");
            throw new NullPointerException("Email not must be null");
        }
        return null;
    }

    public UserProfileResponse getUserProfile(String email) {
        log.info("getUserProfile() -> start with email: " + email);
        Optional<User> byEmail = userRepo.findByEmail(email);
        User user = byEmail.orElseThrow(EntityNotFoundException::new);
        log.info("getUserProfile() -> map User to UserProfile");
        UserProfileResponse profile = UserMapper.MAPPER.userToUserProfile(user);
        log.info("getUserProfile() -> exit");
        return profile;

    }

    public void saveUserConfirmCode(User userByEmail, String confirmCode) {
        log.info("saveUserConfirmCode() -> start with id: " + userByEmail.getId());
        userByEmail.setConfirmEmail(confirmCode);
        log.info("saveUserConfirmCode() -> update user with confirmCode: " + confirmCode);
        userRepo.save(userByEmail);
        log.info("saveUserConfirmCode() -> exit");
    }

    public void successLogin(User userByEmail) {
        log.info("successLogin() -> start with email: " + userByEmail.getUsername());
        userByEmail.setDateTimeOfLastLogin(Instant.now());
        log.info("saveUserConfirmCode() -> clear confirmEmail code");
        saveUserConfirmCode(userByEmail, null);
        log.info("saveUserConfirmCode() -> exit");
    }

    public void updateUserProfile(String username, UserProfileRequest profileRequest) {

    }
}
