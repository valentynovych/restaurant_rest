package com.restaurant_rest.service;

import com.restaurant_rest.entity.Product;
import com.restaurant_rest.entity.RefreshToken;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.entity.UserDetails;
import com.restaurant_rest.mapper.ProductMapper;
import com.restaurant_rest.mapper.UserMapper;
import com.restaurant_rest.model.authetnticate.JwtResponse;
import com.restaurant_rest.model.user.ProductWishListWrap;
import com.restaurant_rest.model.user.UserProfileRequest;
import com.restaurant_rest.model.user.UserProfileResponse;
import com.restaurant_rest.repositoty.UserRepo;
import com.restaurant_rest.utils.JwtTokenUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepo userRepo;
    private final JwtTokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public User getUserByEmail(String email) {
        log.info("getUserByEmail() -> start with email: " + email);
        if (email != null && !email.isEmpty()) {
            Optional<User> byEmail = userRepo.findByEmail(email);
            User user = byEmail.orElseThrow(() -> new EntityNotFoundException(
                    String.format("Користувача з email: %s не знайдено", email)));
            log.info("getUserByEmail() -> user is present, return user");
            return user;
        } else {
            log.error("getUserByEmail() -> user is empty, throw new NullPointerException()");
            throw new NullPointerException("Email not must be null");
        }
    }

    public UserProfileResponse getUserProfile(String email) {
        log.info("getUserProfile() -> start with email: " + email);
        Optional<User> byEmail = userRepo.findByEmail(email);
        User user = byEmail.orElseThrow(() -> new EntityNotFoundException(
                String.format("Користувача з email: %s не знайдено", email)));
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

    public JwtResponse successLogin(User userByEmail) {
        log.info("successLogin() -> start with email: " + userByEmail.getUsername());
        userByEmail.setDateTimeOfLastLogin(Instant.now());
        log.info("saveUserConfirmCode() -> clear confirmEmail code");
        saveUserConfirmCode(userByEmail, null);
        log.info("saveUserConfirmCode() -> exit");
        org.springframework.security.core.userdetails.UserDetails userDetails =
                userDetailsService.loadUserByUsername(userByEmail.getEmail());
        String accessToken = tokenUtils.createAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return JwtResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public UserProfileResponse updateUserProfile(String username, UserProfileRequest profileRequest) {
        log.info("updateUserProfile() -> start with username: " + username);
        User user = getUserByEmail(username);
        UserDetails userDetails = user.getUserDetails();
        userDetails.setFullName(profileRequest.getFullName());
        userDetails.setPhone(profileRequest.getPhone());
        userDetails.setDateOfBirth(profileRequest.getDateOfBirth());
        userDetails.setFacebookUsername(profileRequest.getFacebookUsername());
        userDetails.setPasswordToChange(profileRequest.getPasswordToChange());
        user.setUserDetails(userDetails);
        user.setEmail(profileRequest.getEmail());


        User save = userRepo.save(user);
        log.info("updateUserProfile() -> save user with new data on userDetails with email");
        UserProfileResponse userProfileResponse = UserMapper.MAPPER.userToUserProfile(save);
        log.info("updateUserProfile() -> exit, return user profile");
        return userProfileResponse;
    }

    public ProductWishListWrap updateUserProductWishList(String username, ProductWishListWrap productWishList) {
        log.info("updateUserProductWishList() -> save user with username: " + username);
        User user = getUserByEmail(username);
        log.info("updateUserProductWishList() -> user product wishlist already size: " + user.getProductWishlist().size());
        List<Product> productWishlist = ProductMapper.MAPPER.productShortListToProductList(
                productWishList.getProductWishlist());
        user.setProductWishlist(productWishlist);
        log.info("updateUserProductWishList() -> save user with new product wishlist, size: " + productWishlist.size());
        User save = userRepo.save(user);
        List<Product> newWishlist = save.getProductWishlist();
        productWishList.setProductWishlist(ProductMapper.MAPPER.productListToProductShortList(
                newWishlist));
        log.info("updateUserProductWishList() -> user product wishlist already size: " + newWishlist.size());
        log.info("updateUserProductWishList() -> exit");
        return productWishList;
    }

    public void registerNewUser(String email1, String confirmCode) {
        Optional<User> byEmail = userRepo.findByEmail(email1);
        if (byEmail.isEmpty()) {
            User newUser = new User();
            newUser.setIsActive(Boolean.TRUE);
            newUser.setEmail(email1);
            newUser.setConfirmEmail(confirmCode);
            userRepo.save(newUser);
        } else {
            throw new EntityExistsException("Користувач уже зареєтрований у системі, скористайтесь входом у додаток");
        }
    }
}
