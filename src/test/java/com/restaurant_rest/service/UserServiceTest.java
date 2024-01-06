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
import com.restaurant_rest.repositoty.ProductRepo;
import com.restaurant_rest.repositoty.UserRepo;
import com.restaurant_rest.utils.JwtTokenUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private ProductRepo productRepo;
    @InjectMocks
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setTotalOrders(10);
        user.setConfirmEmail("1234");
        UserDetails userDetails = new UserDetails();
        userDetails.setFullName("Full name");
        userDetails.setPhone("380501401414");
        userDetails.setDateOfBirth(new Date());
        userDetails.setFacebookUsername("facebookUsername");
        userDetails.setActiveBonuses(250);
        userDetails.setPasswordToChange("password");
        user.setUserDetails(userDetails);
    }

    @Test
    void getUserByEmail_PositiveScenarios() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        User user1 = userService.getUserByEmail(user.getEmail());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void getUserByEmail_whenUsernameIsNull() {
        assertThrows(NullPointerException.class, () -> userService.getUserByEmail(null));
        assertThrows(NullPointerException.class, () -> userService.getUserByEmail(""));
    }

    @Test
    void getUserByEmail_whenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
            userService.getUserByEmail(user.getEmail());
        });
    }

    @Test
    void getUserProfile_positiveScenarios() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserProfileResponse user1 = userService.getUserProfile(user.getEmail());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void getUserProfile_whenUserNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserProfile(user.getEmail()));
    }

    @Test
    void saveUserConfirmCode() {
        userService.saveUserConfirmCode(user, user.getConfirmEmail());
        verify(userRepo).save(user);
    }

    @Test
    void successLogin() {
        org.springframework.security.core.userdetails.UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), "null", Collections.singleton(new SimpleGrantedAuthority("USER")));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshToken");
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(userDetails);
        when(jwtTokenUtils.createAccessToken(userDetails)).thenReturn("accessToken");
        when(refreshTokenService.createRefreshToken(userDetails.getUsername())).thenReturn(refreshToken);
        JwtResponse jwtResponse = userService.successLogin(user);
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());
    }

    @Test
    void updateUserProfile() {
        UserProfileRequest request = new UserProfileRequest();
        UserDetails userDetails = user.getUserDetails();
        request.setEmail(user.getEmail());
        request.setPhone(userDetails.getPhone());
        request.setFullName(userDetails.getFullName());
        request.setFacebookUsername(userDetails.getFacebookUsername());
        request.setDateOfBirth(userDetails.getDateOfBirth());
        request.setPasswordToChange(userDetails.getPasswordToChange());
        UserProfileResponse expected = UserMapper.MAPPER.userToUserProfile(user);

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        UserProfileResponse response = userService.updateUserProfile(user.getEmail(), request);
        assertEquals(expected.getFullName(), response.getFullName());
        assertEquals(expected.getPhone(), response.getPhone());
        assertEquals(expected.getActiveBonuses(), response.getActiveBonuses());
        assertEquals(expected.getAmountOfOrders(), response.getAmountOfOrders());
    }

    @Test
    void updateUserProductWishList() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setId((long) i);
            product.setIsIngredient(Boolean.FALSE);
            products.add(product);
        }
        List<Long> list = products.stream().map(Product::getId).toList();
        ProductWishListWrap wishListWrap = new ProductWishListWrap();
        wishListWrap.setProductWishlist(ProductMapper.MAPPER.productListToProductShortList(products));
        user.setProductWishlist(products);

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(productRepo.findByIdIn(list)).thenReturn(products);
        ProductWishListWrap wishListWrap1 = userService.updateUserProductWishList(user.getEmail(), wishListWrap);

        assertFalse(wishListWrap1.getProductWishlist().isEmpty());
        assertEquals(5, wishListWrap1.getProductWishlist().size());
    }

    @Test
    void registerNewUser_ifUserIsPresent() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(EntityExistsException.class, () -> userService.registerNewUser(user.getEmail(), user.getConfirmEmail()));
    }

    @Test
    void registerNewUser_ifUserIsEmpty() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        userService.registerNewUser(user.getEmail(), user.getConfirmEmail());
        verify(userRepo).save(any(User.class));
    }
}