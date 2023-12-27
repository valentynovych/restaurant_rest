package com.restaurant_rest.service;

import com.restaurant_rest.entity.RefreshToken;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.exception.RefreshTokenException;
import com.restaurant_rest.repositoty.RefreshTokenRepo;
import com.restaurant_rest.repositoty.UserRepo;
import com.restaurant_rest.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepo tokenRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private JwtTokenUtils mockJwtUtils;
    private JwtTokenUtils jwtTokenUtils;
    private UserDetailsServiceImpl userDetailsService;
    private RefreshTokenService tokenService;
    private RefreshToken refreshToken;
    private User user;
    Duration refreshTokenLifetime = Duration.of(24, ChronoUnit.HOURS);

    @BeforeEach
    void setUp() {
        jwtTokenUtils = new JwtTokenUtils();
        userDetailsService = new UserDetailsServiceImpl(userRepo);
        tokenService = new RefreshTokenService(
                tokenRepo,
                userRepo,
                mockJwtUtils,
                userDetailsService);
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");

        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
    }

    @Test
    void createRefreshToken_ifOptionalIsPresent() {

        ReflectionTestUtils.setField(tokenService, "refreshTokenLifetime", refreshTokenLifetime);
        ReflectionTestUtils.setField(mockJwtUtils, "refreshTokenLifetime", refreshTokenLifetime);
        ReflectionTestUtils.setField(jwtTokenUtils, "refreshTokenLifetime", refreshTokenLifetime);
        ReflectionTestUtils.setField(mockJwtUtils, "tokenSecret", "secretForJWTWithoutHS256Encoding");
        ReflectionTestUtils.setField(jwtTokenUtils, "tokenSecret", "secretForJWTWithoutHS256Encoding");
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepo.findRefreshTokenByUser(any(User.class))).thenReturn(Optional.of(refreshToken));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        when(mockJwtUtils.createRefreshToken(userDetails)).thenReturn(jwtTokenUtils.createRefreshToken(userDetails));
        refreshToken.setToken(jwtTokenUtils.createRefreshToken(userDetails));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenLifetime.toMillis()));
        when(tokenRepo.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken refreshToken1 = tokenService.createRefreshToken(user.getUsername());
        verify(tokenRepo).save(refreshToken);
        verify(mockJwtUtils).createRefreshToken(userDetails);
        jwtTokenUtils.validateJwtToken(refreshToken1.getToken());
        assertEquals(refreshToken.getToken(), refreshToken1.getToken());
        assertEquals(refreshToken.getUser(), refreshToken1.getUser());
        assertEquals(refreshToken.getExpiryDate(), refreshToken1.getExpiryDate());
    }

    @Test
    void verifyExpiration_ifExpirationIsVerify() {
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenLifetime.toMillis()));
        RefreshToken refreshToken1 = tokenService.verifyExpiration(refreshToken);
        assertEquals(refreshToken.getExpiryDate(), refreshToken1.getExpiryDate());
    }

    @Test
    void verifyExpiration_ifIsExpired() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(refreshTokenLifetime.toMillis()));
        assertThrows(RefreshTokenException.class, () -> tokenService.verifyExpiration(refreshToken));
        verify(tokenRepo).delete(refreshToken);
    }

    @Test
    void findByToken() {
        when(tokenRepo.findRefreshTokenByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));
        Optional<RefreshToken> refreshTokenByToken = tokenService.findByToken(refreshToken.getToken());
        assertTrue(refreshTokenByToken.isPresent());
    }
}