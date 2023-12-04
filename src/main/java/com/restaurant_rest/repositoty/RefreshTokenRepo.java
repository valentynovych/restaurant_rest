package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.RefreshToken;
import com.restaurant_rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findRefreshTokenByUser(User user);

    Optional<RefreshToken> findRefreshTokenByToken(String token);

    int deleteByUser(User user);

    boolean existsByUser(User user);
}
