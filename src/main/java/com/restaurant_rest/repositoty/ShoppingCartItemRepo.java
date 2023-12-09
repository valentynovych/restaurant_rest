package com.restaurant_rest.repositoty;

import com.restaurant_rest.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartItemRepo extends JpaRepository<ShoppingCartItem, Long>, JpaSpecificationExecutor<ShoppingCartItem> {
}
