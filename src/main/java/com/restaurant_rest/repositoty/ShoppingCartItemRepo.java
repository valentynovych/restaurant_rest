package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.ShoppingCartItem;
import com.restaurant_rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartItemRepo extends JpaRepository<ShoppingCartItem, Long>, JpaSpecificationExecutor<ShoppingCartItem> {
    Optional<ShoppingCartItem> findByUserAndId(User user, Long id);
    List<ShoppingCartItem> findAllByUser(User user);

}
