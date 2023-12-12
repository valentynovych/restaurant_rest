package com.restaurant_rest.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class JwtTokenUtils {
    @Value("${token.secret}")
    private String tokenSecret;
    @Value("${token.lifetime.accessToken}")
    private Duration accessTokenLifetime;
    @Value("${token.lifetime.refreshToken}")
    private Duration refreshTokenLifetime;

    public String createAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenLifetime);
    }

    public String createRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenLifetime);
    }

    public String generateToken(UserDetails userDetails, Duration duration) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roleList = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roleList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + duration.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getAllClaims(token).get("roles", List.class);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateJwtToken(String jwtToken) {
        Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(jwtToken);
        return true;
    }
}
