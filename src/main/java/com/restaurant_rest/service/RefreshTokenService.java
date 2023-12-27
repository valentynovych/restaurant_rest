package com.restaurant_rest.service;

import com.restaurant_rest.entity.RefreshToken;
import com.restaurant_rest.entity.User;
import com.restaurant_rest.exception.RefreshTokenException;
import com.restaurant_rest.repositoty.RefreshTokenRepo;
import com.restaurant_rest.repositoty.UserRepo;
import com.restaurant_rest.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsServiceImpl userDetailsService;
    @Value("${token.lifetime.refreshToken}")
    private Duration refreshTokenLifetime;

    public RefreshToken createRefreshToken(String username) {
        log.info("createRefreshToken() -> start with email: " + username);
        User user = userRepo.findByEmail(username).get();
        RefreshToken refreshToken = refreshTokenRepo.findRefreshTokenByUser(user)
                .orElse(new RefreshToken());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenLifetime.toMillis()));
        refreshToken.setToken(jwtTokenUtils.createRefreshToken(userDetailsService.loadUserByUsername(username)));

        log.info("createRefreshToken() -> save refreshToken: " + refreshToken);
        refreshToken = refreshTokenRepo.save(refreshToken);
        log.info("createRefreshToken() -> exit");
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        log.info("verifyExpiration() -> start with token: " + token);
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            log.info("verifyExpiration() -> Refresh token was expired, delete from DB");
            refreshTokenRepo.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token %s was expired. Please make a new signin request");
        }
        log.info("verifyExpiration() -> token is alive, return: " + token);
        return token;
    }

    public Optional<RefreshToken> findByToken(String requestRefreshToken) {
        return refreshTokenRepo.findRefreshTokenByToken(requestRefreshToken);
    }
}
